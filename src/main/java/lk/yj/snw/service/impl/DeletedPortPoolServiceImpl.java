package lk.yj.snw.service.impl;

import lk.yj.snw.model.PortPool;
import lk.yj.snw.repositary.DeletedPortPoolRepository;
import lk.yj.snw.service.DeletedPortPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DeletedPortPoolServiceImpl implements DeletedPortPoolService {

    @Autowired
    DeletedPortPoolRepository deletedPortPoolRepository;

    @Override
    public Collection<PortPool> findAll() {
        return deletedPortPoolRepository.findAll();
    }

    @Override
    public void delete(int id) {
        deletedPortPoolRepository.deleteById(id);
    }
}
