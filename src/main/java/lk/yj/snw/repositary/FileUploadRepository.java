package lk.yj.snw.repositary;

import lk.yj.snw.model.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends JpaRepository<UploadedFile, Integer> {

    @Query("SELECT MAX(uf.portNumber) FROM UploadedFile uf")
    Integer maxPortNumber();

    @Query("SELECT count(uf) > 0 FROM UploadedFile uf WHERE uf.origin = :origin AND uf.userId = :userId")
    boolean existByOriginAndUserId(@Param("origin") String origin, @Param("userId") int userId);

    UploadedFile findOneByUserIdAndOrigin(int userId, String origin);

    UploadedFile findOneByOrigin(String origin);

    @Query("SELECT COUNT(uf) FROM UploadedFile uf WHERE uf.userId = :userId AND uf.serviceType = 'external'")
    int getTotalExternalServiceCountForUser(@Param("userId") int userId);
}
