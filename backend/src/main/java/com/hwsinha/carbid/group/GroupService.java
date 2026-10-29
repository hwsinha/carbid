package com.hwsinha.carbid.group;

import com.hwsinha.carbid.car.Car;
import com.hwsinha.carbid.car.CarRepository;
import com.hwsinha.carbid.group.dto.CreateGroupRequest;
import com.hwsinha.carbid.group.dto.GroupResponse;
import com.hwsinha.carbid.group.dto.JoinGroupRequest;
import com.hwsinha.carbid.user.User;
import com.hwsinha.carbid.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final BuyGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {
        Car car= carRepository.findById(request.carId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        User creator= userRepository.findById(request.createdById())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        BuyGroup group=new BuyGroup();
        group.setCar(car);
        group.setCity(request.city());
        group.setMinMembers(request.minMembers());
        group.setDeadline(request.deadline());
        group.setCreatedBy(creator);
        group.setStatus(GroupStatus.OPEN);

        BuyGroup savedGroup=groupRepository.save(group);

        GroupMember member=new GroupMember();
        member.setGroup(savedGroup);
        member.setUser(creator);
        memberRepository.save(member);

        return toResponse(savedGroup);
    }

    @Transactional
    public GroupResponse joinGroup(Long groupId, JoinGroupRequest request) {
        BuyGroup group=groupRepository.findById(groupId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        User user=userRepository.findById(request.userId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (group.getStatus()!= GroupStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Group is not open");
        }
        if(group.getDeadline().isBefore(Instant.now())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Deadline is passed");
        }
        if(memberRepository.existsByGroupIdAndUserId(groupId,user.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "already a member");
        }
        GroupMember member=new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        memberRepository.saveAndFlush(member);
        if (memberRepository.countByGroupId(groupId)>= group.getMinMembers()){
            group.setStatus(GroupStatus.BIDDING);
        }
        return toResponse(group);
    }

    private GroupResponse toResponse(BuyGroup group) {
        return  new GroupResponse(
                group.getId(),
                group.getCar().getId(),
                group.getCity(),
                group.getStatus(),
                group.getMinMembers(),
                group.getDeadline()
        );
    }
}
