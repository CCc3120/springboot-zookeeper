package com.bingo.zk.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "fruit")
// @Proxy(lazy = false)
public class Fruit {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String fdId;

	@Column(name = "fd_name")
	private String fdName;

	@Column(name = "fd_price")
	private Double fdPrice;

	@Column(name = "fd_num")
	private Integer fdNum;

	public Fruit() {
		super();
	}

	public Fruit(String fdId, String fdName, Double fdPrice, Integer fdNum) {
		super();
		this.fdId = fdId;
		this.fdName = fdName;
		this.fdPrice = fdPrice;
		this.fdNum = fdNum;
	}

	public String getFdId() {
		return fdId;
	}

	public void setFdId(String fdId) {
		this.fdId = fdId;
	}

	public String getFdName() {
		return fdName;
	}

	public void setFdName(String fdName) {
		this.fdName = fdName;
	}

	public Double getFdPrice() {
		return fdPrice;
	}

	public void setFdPrice(Double fdPrice) {
		this.fdPrice = fdPrice;
	}

	public Integer getFdNum() {
		return fdNum;
	}

	public void setFdNum(Integer fdNum) {
		this.fdNum = fdNum;
	}

	@Override
	public String toString() {
		return "Fruit [fdId=" + fdId + ", fdName=" + fdName + ", fdPrice=" + fdPrice + ", fdNum=" + fdNum + "]";
	}

}
