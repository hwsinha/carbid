package com.hwsinha.carbid.group;

import com.hwsinha.carbid.bid.BidRepository;
import com.hwsinha.carbid.car.Car;
import com.hwsinha.carbid.car.CarRepository;
import com.hwsinha.carbid.user.User;
import com.hwsinha.carbid.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    private static final Long GROUP_ID = 1L;
    private static final Long USER_ID = 5L;

    @Mock BuyGroupRepository groupRepository;
    @Mock GroupMemberRepository memberRepository;
    @Mock CarRepository carRepository;
    @Mock UserRepository userRepository;
    @Mock BidRepository bidRepository;

    @InjectMocks GroupService groupService;

    private BuyGroup openGroup(int minMembers) {
        Car car = new Car();
        car.setId(1L);

        BuyGroup group = new BuyGroup();
        group.setId(GROUP_ID);
        group.setCar(car);
        group.setCity("Mumbai");
        group.setMinMembers(minMembers);
        group.setStatus(GroupStatus.OPEN);
        group.setDeadline(Instant.now().plus(Duration.ofDays(1)));
        return group;
    }

    private User buyer() {
        User user = new User();
        user.setId(USER_ID);
        return user;
    }

    private void groupAndUserExist(BuyGroup group) {
        when(groupRepository.findByIdForUpdate(GROUP_ID)).thenReturn(Optional.of(group));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(buyer()));
    }


    @Test
    void joinGroup_movesToBidding_whenMinimumReached() {
        BuyGroup group = openGroup(3);
        groupAndUserExist(group);
        when(memberRepository.countByGroupId(GROUP_ID)).thenReturn(3L);

        groupService.joinGroup(GROUP_ID, USER_ID);

        assertEquals(GroupStatus.BIDDING, group.getStatus());
        assertNotNull(group.getBiddingDeadline());
        verify(memberRepository).saveAndFlush(any(GroupMember.class));
    }

    @Test
    void joinGroup_staysOpen_whenBelowMinimum() {
        BuyGroup group = openGroup(3);
        groupAndUserExist(group);
        when(memberRepository.countByGroupId(GROUP_ID)).thenReturn(2L);

        groupService.joinGroup(GROUP_ID, USER_ID);

        assertEquals(GroupStatus.BIDDING, group.getStatus());
        assertNull(group.getBiddingDeadline());
        verify(memberRepository).saveAndFlush(any(GroupMember.class));
    }

    @Test
    void joinGroup_rejects_whenAlreadyMember() {
        groupAndUserExist(openGroup(3));
        when(memberRepository.existsByGroupIdAndUserId(GROUP_ID, USER_ID)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> groupService.joinGroup(GROUP_ID, USER_ID));

        assertEquals(409, ex.getStatusCode().value());
        assertEquals("already a member", ex.getReason());
        verify(memberRepository, never()).saveAndFlush(any());
    }

    @Test
    void joinGroup_rejects_whenGroupNotOpen() {
        BuyGroup group = openGroup(3);
        group.setStatus(GroupStatus.BIDDING);
        groupAndUserExist(group);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> groupService.joinGroup(GROUP_ID, USER_ID));

        assertEquals(409, ex.getStatusCode().value());
        verify(memberRepository, never()).saveAndFlush(any());
    }

    @Test
    void joinGroup_rejects_whenDeadlinePassed() {
        BuyGroup group = openGroup(3);
        group.setDeadline(Instant.now().minus(Duration.ofHours(1)));
        groupAndUserExist(group);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> groupService.joinGroup(GROUP_ID, USER_ID));

        assertEquals(409, ex.getStatusCode().value());
        verify(memberRepository, never()).saveAndFlush(any());
    }

    @Test
    void joinGroup_returns404_whenGroupMissing() {
        when(groupRepository.findByIdForUpdate(GROUP_ID)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> groupService.joinGroup(GROUP_ID, USER_ID));

        assertEquals(404, ex.getStatusCode().value());
        verifyNoInteractions(userRepository, memberRepository);
    }
}