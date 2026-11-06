package com.hwsinha.carbid.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember,Long> {

    boolean existsByGroupIdAndUserId(Long groupId,Long userId);
    long countByGroupId(Long groupId);
    @Query("select m.group.id as groupId, count(m) as cnt from GroupMember m where m.group.id in :ids group by m.group.id")
    List<GroupMemberCount> countByGroupIds(@Param("ids") List<Long> ids);
}
