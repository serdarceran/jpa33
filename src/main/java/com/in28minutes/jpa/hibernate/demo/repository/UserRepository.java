package com.in28minutes.jpa.hibernate.demo.repository;

import java.util.List;

import com.in28minutes.jpa.hibernate.demo.entity.Course;

import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<Course, Long>, CustomRepo {

  List<Course> findByName(String name);
}