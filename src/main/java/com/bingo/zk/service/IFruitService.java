package com.bingo.zk.service;

import java.util.List;

import com.bingo.zk.model.Fruit;

public interface IFruitService {
	public Fruit save(Fruit fruit);

	public Fruit findByPrimarykey(String fdId);

	public Fruit update(Fruit fruit);

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
