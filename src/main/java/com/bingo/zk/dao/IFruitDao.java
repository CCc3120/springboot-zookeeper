package com.bingo.zk.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bingo.zk.model.Fruit;

public interface IFruitDao extends JpaRepository<Fruit, String>, JpaSpecificationExecutor<Fruit> {

	public Fruit findByFdName(String fdName);

	public Fruit findFirstByOrderByFdNum();

	public Fruit findTopByOrderByFdNumDesc();

	public Fruit findByFdNameOrFdNum(String fdName, Integer fdNum);

	public Fruit findByFdNameAndFdNum(String fdName, Integer fdNum);

	public List<Fruit> findByFdNameLike(String fdName);

	public List<Fruit> findByFdNameContaining(String fdName);

	public Long deleteByFdId(String fdId);

	public Long countByFdName(String fdName);

}
