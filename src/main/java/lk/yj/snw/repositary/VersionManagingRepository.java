package lk.yj.snw.repositary;

import lk.yj.snw.model.VersionManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public interface VersionManagingRepository extends JpaRepository<VersionManagement, Integer> {

    @Query("SELECT count(vm) > 0 FROM VersionManagement vm WHERE vm.dockerName = :dockerName")
    boolean isVersionExists(@Param("dockerName") String dockerName);

    @Query("SELECT vm FROM VersionManagement vm WHERE vm.uploadedFile.id = :id")
    Collection<VersionManagement> findAllByUploadedFileId(@Param("id") int id);

    @Query("SELECT MAX(vm.version) FROM VersionManagement vm WHERE vm.uploadedFile.id = :id")
    int maxVersionByUploadedFileId(@Param("id") int id);

    @Modifying
    @Transactional
    @Query("UPDATE VersionManagement vm SET vm.active = false WHERE vm.uploadedFile.id = :id")
    void deActivateAllByUploadedFileId(@Param("id") int id);
}
