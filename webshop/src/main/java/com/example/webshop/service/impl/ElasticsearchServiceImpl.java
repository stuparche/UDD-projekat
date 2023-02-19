package com.example.webshop.service.impl;

import com.example.webshop.dto.SearchQueryDTO;
import com.example.webshop.helper.SearchUtil;
import com.example.webshop.model.Candidate;
import com.example.webshop.repository.CandidateDocumentRepository;
import com.example.webshop.repository.CandidateRepository;
import com.example.webshop.service.ElasticsearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.elasticsearch.search.SearchHit;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private static Logger LOG = LoggerFactory.getLogger(IndexServiceImpl.class);

    @Autowired
    CandidateRepository candidateRepository;
    @Autowired
    CandidateDocumentRepository candidateDocumentRepository;

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    ElasticsearchServiceImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public ResponseEntity<String> executeSearchQuery(SearchQueryDTO searchQuery) throws Exception {
        SearchRequest request = SearchUtil.buildSearchRequest(searchQuery);
        if (request == null) {
            LOG.error("Search request creation failed");
            return null;
        }

        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] searchHits = response.getHits().getHits();
        } catch (Error e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
        return null;
    }

    @Override
    public ResponseEntity<String> populateIndexFromDatabase() throws Exception {
        List<Candidate> candidates = candidateRepository.findAll();
        return null;
    }
}
