package com.hwsinha.carbid.group;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember,Long> {

    boolean existsByGroupIdAndUserId(Long groupId,Long userId);
    long countByGroupId(Long groupId);
}
