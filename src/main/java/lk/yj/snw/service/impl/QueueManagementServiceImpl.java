package lk.yj.snw.service.impl;

import lk.yj.snw.model.QueueDetails;
import lk.yj.snw.repositary.QueueManagementRepository;
import lk.yj.snw.service.QueueManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class QueueManagementServiceImpl implements QueueManagementService {

    @Autowired
    QueueManagementRepository queueManagementRepository;


    @Override
    public QueueDetails create(QueueDetails queueDetails) {

        if (queueDetails.getId() != 0) {
            return null;
        }

        return queueManagementRepository.save(queueDetails);
    }

}
