package com.prodyna.pac.vote.service.impl;

import com.prodyna.pac.vote.annotations.AuthenticatedUser;
import com.prodyna.pac.vote.annotations.Monitored;
import com.prodyna.pac.vote.service.api.VoteService;
import com.prodyna.pac.vote.service.api.dto.VoteDto;
import com.prodyna.pac.vote.service.api.model.Ballot;
import com.prodyna.pac.vote.service.api.model.Choice;
import com.prodyna.pac.vote.service.api.model.ChoiceCount;
import com.prodyna.pac.vote.service.api.model.User;
import com.prodyna.pac.vote.service.api.model.Vote;
import com.prodyna.pac.vote.service.exceptions.InvalidChoiceException;
import com.prodyna.pac.vote.service.exceptions.UnknownBallotException;
import com.prodyna.pac.vote.service.exceptions.UnknownChoiceException;
import com.prodyna.pac.vote.service.exceptions.UserAlreadyVotedException;
import com.prodyna.pac.vote.service.exceptions.UserNotVotedException;
import com.prodyna.pac.vote.service.repo.api.BallotRepository;
import com.prodyna.pac.vote.service.repo.api.ChoiceRepository;
import com.prodyna.pac.vote.service.repo.api.UserRepository;
import com.prodyna.pac.vote.service.repo.api.VoteRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

/**
 *
 * @author cschaefer
 *
 */
@Monitored
@Provider
@Stateless
public class VoteServiceImpl implements VoteService {


    @Inject
    private Logger log;

    @Inject
    @AuthenticatedUser
    private User authenticatedUser;

    @Inject
    private VoteRepository voteRepo;

    @Inject
    private BallotRepository ballotRepository;

    @Inject
    private ChoiceRepository choiceRepository;

    @Inject
    private UserRepository userRepository;

    @Override
    public Vote vote(VoteDto voteDto) throws UnknownBallotException, UnknownChoiceException, InvalidChoiceException, UserAlreadyVotedException {

        if (Objects.isNull(voteDto.getBallotId())) {
            throw new UnknownBallotException("No ballot found with id " + voteDto.getBallotId());
        }

        if (Objects.isNull(voteDto.getChoiceId())) {
            throw new UnknownChoiceException("No choice found with id " + voteDto.getChoiceId());
        }


        final Ballot ballot = this.ballotRepository.findById(voteDto.getBallotId());
        final Choice choice = this.choiceRepository.findById(voteDto.getChoiceId());

        if (Objects.isNull(ballot)) {
            throw new UnknownBallotException("No ballot found with id " + voteDto.getBallotId());
        }

        if (Objects.isNull(choice)) {
            throw new UnknownChoiceException("No choice found with id " + voteDto.getChoiceId());
        }

        if (Objects.isNull(ballot.getChoices())) {
            throw new UnknownChoiceException("Ballot with id " + voteDto.getBallotId() + " contains no choices");
        }

        if (!ballot.getChoices().contains(choice)) {
            throw new InvalidChoiceException("Choice with id " + voteDto.getChoiceId() + " does not belong to ballot with id " + voteDto.getBallotId());
        }

        if (this.voteRepo.hasUserVoted(this.authenticatedUser.getUserId(), ballot.getBallotId())) {
            throw new UserAlreadyVotedException("User has already voted!");
        }

        final User voter = this.userRepository.findById(this.authenticatedUser.getUserId());

        final Vote vote = new Vote();
        vote.setBallotId(ballot.getBallotId());
        vote.setChoiceId(choice.getChoiceId());
        vote.setVoteDate(LocalDate.now());
        //vote.setVoter(authenticatedUser); TODO work out why jackson cannot serialize
        vote.setUserId(voter.getUserId());

        final Vote createdVote = this.voteRepo.create(vote);
        return createdVote;
    }

    @Override
    public List<VoteDto> findMyVotes() {

        final Integer userId = this.authenticatedUser.getUserId();

        final List<Vote> myVotes = this.voteRepo.findMyBallotVotes(userId);

        final List<VoteDto> voteList = new ArrayList<VoteDto>();
        for (final Vote myVote : myVotes) {
            final VoteDto voteDto = new VoteDto();
            voteDto.setBallotId(myVote.getBallotId());
            voteDto.setChoiceId(myVote.getChoiceId());
            voteList.add(voteDto);
        }

        if (this.log.isDebugEnabled()) {
            this.log.debug("User {} has voted {}", this.authenticatedUser, voteList);
        }

        return voteList;
    }

    @Override
    public List<ChoiceCount> findBallotVotes(Integer ballotId) throws UnknownBallotException, UserNotVotedException {

        final Ballot ballot = this.ballotRepository.findById(ballotId);
        if (Objects.isNull(ballot)) {
            throw new UnknownBallotException("Ballot " + ballotId + " does not exist");
        }

        if (!this.voteRepo.hasUserVoted(this.authenticatedUser.getUserId(), ballotId)) {
            this.log.error("User [{}] has not voted on ballot [{}]", this.authenticatedUser, ballot);
            throw new UserNotVotedException("User has not voted!");
        }

        final List<ChoiceCount> choiceCounts = this.voteRepo.findBallotResult(ballotId);

        final Map<Integer, ChoiceCount> choiceCountMap = new HashMap<Integer, ChoiceCount>();
        for (final ChoiceCount choiceCount : choiceCounts) {
            choiceCountMap.put(choiceCount.getChoiceId(), choiceCount);
        }

        for (final Choice choice : ballot.getChoices()) {
            if (!choiceCountMap.containsKey(choice.getChoiceId())) {
                final ChoiceCount emptyChoiceCount = new ChoiceCount();
                emptyChoiceCount.setChoiceId(choice.getChoiceId());
                emptyChoiceCount.setCount(0L);
                choiceCounts.add(emptyChoiceCount);
            }
        }

        if (this.log.isDebugEnabled()) {
            this.log.debug("Ballot [{}] has results [{}]", ballot, choiceCounts);
        }

        return choiceCounts;
    }

}
