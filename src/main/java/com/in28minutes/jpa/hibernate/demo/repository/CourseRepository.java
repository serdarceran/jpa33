package com.in28minutes.jpa.hibernate.demo.repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.in28minutes.jpa.hibernate.demo.entity.Course;
import com.in28minutes.jpa.hibernate.demo.entity.Review;

@Repository
@Transactional
public class CourseRepository {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	EntityManager em;

	public Course findById(Long id) {
		return em.find(Course.class, id);
	}

	public Course save(Course course) {

		if (course.getId() == null) {
			em.persist(course);
		} else {
			em.merge(course);
		}

		return course;
	}

	public void deleteById(Long id) {
		Course course = findById(id);
		em.remove(course);
	}

	public void playWithEntityManager() {
		Course course1 = new Course("Web Services in 100 Steps");
		em.persist(course1);
		
		Course course2 = findById(10001L);
		
		course2.setName("JPA in 50 Steps - Updated");
		
	}

	public void addHardcodedReviewsForCourse() {
		//get the course 10003
		Course course = findById(10003L);
		logger.info("course.getReviews() -> {}", course.getReviews());
		
		//add 2 reviews to it
		Review review1 = new Review("5", "Great Hands-on Stuff.");	
		Review review2 = new Review("5", "Hatsoff.");
		
		//setting the relationship
		course.addReview(review1);
		review1.setCourse(course);
		
		course.addReview(review2);
		review2.setCourse(course);
		
		//save it to the database
		em.persist(review1);
		em.persist(review2);
	}
	
	public void addReviewsForCourse(Long courseId, List<Review> reviews) {		
		Course course = findById(courseId);
		logger.info("course.getReviews() -> {}", course.getReviews());
		for(Review review:reviews)
		{			
			//setting the relationship
			course.addReview(review);
			review.setCourse(course);
			em.persist(review);
		}
	}

	public void setCourseWithReviews(final Course course) {		
		
		Course persistedCourse = findById(course.getId());
		HashSet<Review> persistedReviews = persistedCourse.getReviews();

		HashSet<Review> newReviews = (HashSet<Review>) course.getReviews().clone();
		newReviews.removeAll(persistedReviews);

		for(Review newReview:newReviews)
		{			
			//setting the relationship
			persistedCourse.addReview(newReview);
			newReview.setCourse(persistedCourse);
			em.persist(newReview);
		}

		HashSet<Review> deleteReviews = (HashSet<Review>) persistedReviews.clone();
		deleteReviews.removeAll(course.getReviews());
		
		for(Review deleteReview:deleteReviews)
		{			
			//setting the relationship
			persistedCourse.removeReview(deleteReview);
			em.remove(deleteReview);
		}

		HashMap<Long, Review> reviewMap = convert(course.getReviews());
		persistedReviews.stream().filter(pr-> reviewMap.containsKey(pr.getId())).forEach(pr->{
			Review r= reviewMap.get(pr.getId());
			pr.setDescription(r.getDescription());
			pr.setRating(r.getRating());
		});
	}

	HashMap<Long, Review> convert(HashSet<Review> reviewSet) {
		HashMap<Long, Review> reviewMap = new HashMap<Long, Review>();

		for (Review review : reviewSet) {
			reviewMap.put(review.getId(), review);
		}
		return reviewMap;
	}
}