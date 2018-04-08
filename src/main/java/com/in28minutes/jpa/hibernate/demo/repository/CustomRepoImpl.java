package com.in28minutes.jpa.hibernate.demo.repository;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import com.in28minutes.jpa.hibernate.demo.entity.Course;

import org.springframework.beans.factory.annotation.Autowired;



public class CustomRepoImpl implements CustomRepo  {

@Autowired
EntityManager entityManager;

  public List<Course> customFind(String name) {
    entityManager.clear();
	  return Collections.emptyList();

  }
}