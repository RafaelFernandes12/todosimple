package com.javaStudies.todosimple.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.javaStudies.todosimple.models.Task;
import com.javaStudies.todosimple.models.projection.TaskProjection;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	List<TaskProjection> findByUser_Id(long id);

//	@Query(value = "Select t from Task t WHERE t.user.id = :id")
//	List<Task> findByUser_Id(@Param("id") Long id);

}
