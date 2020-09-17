package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.po.UserPo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserPo,Integer> {
    @Override
    List<UserPo> findAll();

    @Override
    Optional<UserPo> findById(Integer integer);
}
