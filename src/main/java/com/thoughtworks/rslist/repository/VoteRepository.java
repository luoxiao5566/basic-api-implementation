package com.thoughtworks.rslist.repository;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thoughtworks.rslist.po.VotePo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


import java.util.Arrays;
import java.util.List;

public interface VoteRepository extends PagingAndSortingRepository<VotePo,Integer> {
    @Override
    List<VotePo> findAll();

    /*@Query("select v from VotePo v where v.user.id = :userId and v.rsEvent.id = :rsEventId")
    List<VotePo> findByUserIdAndRsEventId(int userId, int rsEventId, Pageable pageable);

    @Query("select v from VotePo v where v.localDateTime >= :startTime and v.localDateTime <= :endTime")
    List<VotePo> myLocalDateTime(String startTime, String endTime);*/
}
