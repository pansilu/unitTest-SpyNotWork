package lk.yj.snw.repositary;

import lk.yj.snw.model.PortPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DeletedPortPoolRepository extends JpaRepository<PortPool, Integer> {

}
