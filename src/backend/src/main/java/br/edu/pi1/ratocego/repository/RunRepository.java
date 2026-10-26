package br.edu.pi1.ratocego.repository;

import br.edu.pi1.ratocego.model.Run;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import br.edu.pi1.ratocego.model.RunStatus;

public interface RunRepository extends JpaRepository<Run, Long> {

    List<Run> findByMazeRowsAndMazeColumns(int mazeRows, int mazeColumns, Sort sort);

    List<Run> findByStatusIn(List<RunStatus> statuses);

    java.util.Optional<Run> findFirstByStatusInOrderByIdDesc(List<RunStatus> statuses);
}
