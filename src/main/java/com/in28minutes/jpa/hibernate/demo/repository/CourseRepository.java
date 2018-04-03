package com.in28minutes.jpa.hibernate.demo.repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		Course c = em.find(Course.class, id);
		Set<Review> rs = c.getReviews();
		System.out.println(">> " + em.contains(c));
		rs.stream().forEach(r->{
			System.out.println(">> " + em.contains(r));
		});
		
		em.detach(c);
		System.out.println(">> " + em.contains(c));
		rs.stream().forEach(r->{
			System.out.println(">> " + em.contains(r));
		});
		c.setReviews(rs);
		return c;
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
		Course course = em.find(Course.class, courseId);
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
		
		Course persistedCourse = em.find(Course.class, course.getId());
		Set<Review> persistedReviews = persistedCourse.getReviews();

		Set<Review> newReviews = course.getReviews().stream().collect(Collectors.toSet());
		newReviews.removeAll(persistedReviews);

		for(Review newReview:newReviews)
		{			
			//setting the relationship
			persistedCourse.addReview(newReview);
			newReview.setCourse(persistedCourse);
			em.persist(newReview);
		}

		Set<Review> deleteReviews = persistedReviews.stream().collect(Collectors.toSet());
		deleteReviews.removeAll(course.getReviews());

		for(Review deleteReview:deleteReviews)
		{			
			//setting the relationship
			persistedCourse.removeReview(deleteReview);
			em.remove(deleteReview);
		}

		HashMap<Long, Review> reviewMap = convert(course.getReviews());
		persistedReviews.stream().forEach(pr->{
			Review r = reviewMap.get(pr.getId());
			if(r.equals(pr)) {
				pr.setDescription(r.getDescription());
				pr.setRating(r.getRating());
			}
		});
	}

	HashMap<Long, Review> convert(Set<Review> reviewSet) {
		HashMap<Long, Review> reviewMap = new HashMap<Long, Review>();

		for (Review review : reviewSet) {
			reviewMap.put(review.getId(), review);
		}
		return reviewMap;
	}
}