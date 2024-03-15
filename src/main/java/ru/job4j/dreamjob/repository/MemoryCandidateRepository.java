package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId;

    private final ConcurrentHashMap<Integer, Candidate> candidates;

    private MemoryCandidateRepository() {
        this.nextId = new AtomicInteger(1);
        this.candidates = new ConcurrentHashMap<>();
        save(new Candidate(0, "Rob", "Intern Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "John", "Junior Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Mellisa", "Junior+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Liza", "Middle Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Tom", "Middle+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Samantha", "Senior Java Developer", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(oldCandidate.getId(), candidate.getName(),
                        candidate.getDescription(), candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
