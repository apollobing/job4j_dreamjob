package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleCandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;

    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository candidateRepository, FileService fileService) {
        this.candidateRepository = candidateRepository;
        this.fileService = fileService;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto attachment) {
        saveNewFile(candidate, attachment);
        return candidateRepository.save(candidate);
    }

    private void saveNewFile(Candidate candidate, FileDto attachment) {
        var file = fileService.save(attachment);
        candidate.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        boolean rsl = false;
        var fileOptional = findById(id);
        if (fileOptional.isPresent()) {
            rsl = candidateRepository.deleteById(id);
            fileService.deleteById(fileOptional.get().getFileId());
        }
        return rsl;
    }

    @Override
    public boolean update(Candidate candidate, FileDto attachment) {
        var isNewFileEmpty = attachment.getContent().length == 0;
        if (isNewFileEmpty) {
            return candidateRepository.update(candidate);
        }
        var oldFileId = candidate.getFileId();
        saveNewFile(candidate, attachment);
        var isUpdated = candidateRepository.update(candidate);
        fileService.deleteById(oldFileId);
        return isUpdated;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}