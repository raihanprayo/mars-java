package dev.scaraz.mars.core.repository.db.credential;

import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.RoleComposite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleCompositeRepo extends JpaRepository<RoleComposite, String> {

    @Query("select rc.child from RoleComposite rc where rc.parent.id = :parentId")
    List<Role> findAllByParentId(String parentId);

}
