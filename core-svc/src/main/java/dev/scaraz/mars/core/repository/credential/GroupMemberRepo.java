package dev.scaraz.mars.core.repository.credential;

import dev.scaraz.mars.core.domain.credential.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepo extends JpaRepository<GroupMember, Long> {

    void deleteByUserIdAndGroupId(String userId, String groupId);

}
