package com.gitlab.walneyalves.meeting_rooms.service;

import com.gitlab.walneyalves.meeting_rooms.model.Resource;
import com.gitlab.walneyalves.meeting_rooms.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Transactional
    public Resource create(@NonNull Resource.ResourceBuilder builder) {
        return resourceRepository.save(builder.build());
    }

    public Optional<Resource> get(@NonNull UUID id) {
        return resourceRepository.findById(id);
    }

    public Page<Resource> getAll(@NonNull Pageable pageable) {
        return resourceRepository.findAll(pageable);
    }

    @Transactional
    public Resource updateName(@NonNull Resource resource, @NonNull String name) {
        resource.setName(name);
        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource updateType(@NonNull Resource resource, @NonNull Resource.Type type) {
        resource.setType(type);
        return resourceRepository.save(resource);
    }

    @Transactional
    public void delete(@NonNull Resource resource) {
        resourceRepository.deleteById(resource.getId());
    }
}