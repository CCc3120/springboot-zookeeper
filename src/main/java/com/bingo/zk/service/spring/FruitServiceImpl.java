package com.bingo.zk.service.spring;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bingo.zk.dao.IFruitDao;
import com.bingo.zk.model.Fruit;
import com.bingo.zk.service.IFruitService;

@Service
@Transactional
public class FruitServiceImpl implements IFruitService {

	@Autowired
	private IFruitDao fruitDao;

	@Override
	public Fruit save(Fruit fruit) {
		return fruitDao.save(fruit);
	}

	@Override
	public Fruit findByFdName(String fdName) {
		return fruitDao.findByFdName(fdName);
	}

	@Override
	public Fruit findFirstByOrderByFdNum() {
		return fruitDao.findFirstByOrderByFdNum();
	}

	@Override
	public Fruit findTopByOrderByFdNumDesc() {
		return fruitDao.findTopByOrderByFdNumDesc();
	}

	@Override
	public Fruit findByFdNameOrFdNum(String fdName, Integer fdNum) {
		return fruitDao.findByFdNameOrFdNum(fdName, fdNum);
	}

	@Override
	public Fruit findByFdNameAndFdNum(String fdName, Integer fdNum) {
		return fruitDao.findByFdNameAndFdNum(fdName, fdNum);
	}

	@Override
	public List<Fruit> findByFdNameLike(String fdName) {
		return fruitDao.findByFdNameLike(fdName);
	}

	@Override
	public Long deleteByFdId(String fdId) {
		return fruitDao.deleteByFdId(fdId);
	}

	@Override
	public Long countByFdName(String fdName) {
		return fruitDao.countByFdName(fdName);
	}

	@Override
	public List<Fruit> findByFdNameContaining(String fdName) {
		return fruitDao.findByFdNameContaining(fdName);
	}

	@Override
	public Fruit findByPrimarykey(String fdId) {
		return fruitDao.getOne(fdId);
	}

	@Override
	public Fruit update(Fruit fruit) {
		return fruitDao.save(fruit);
	}
}
