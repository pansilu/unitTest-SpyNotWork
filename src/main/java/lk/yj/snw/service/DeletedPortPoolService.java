package lk.yj.snw.service;

import lk.yj.snw.model.PortPool;

import java.util.Collection;

public interface DeletedPortPoolService {

    Collection<PortPool> findAll();

    void delete(int id);
}
