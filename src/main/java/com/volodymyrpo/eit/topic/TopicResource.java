package com.volodymyrpo.eit.topic;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("topics")
public class TopicResource {

    private final TopicRepository topicRepository;

    public TopicResource(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @GetMapping
    public List<Topic> getAll() {
        return this.topicRepository.findAll();
    }
}
