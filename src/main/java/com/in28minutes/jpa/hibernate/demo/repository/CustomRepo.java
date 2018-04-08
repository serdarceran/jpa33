package com.in28minutes.jpa.hibernate.demo.repository;

import java.util.List;

import com.in28minutes.jpa.hibernate.demo.entity.Course;



public interface CustomRepo  {

  List<Course> customFind(String name);
}