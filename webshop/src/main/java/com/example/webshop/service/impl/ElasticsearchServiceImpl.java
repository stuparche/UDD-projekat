package com.example.webshop.service.impl;

import com.example.webshop.dto.SearchQueryDTO;
import com.example.webshop.dto.SearchResponseDTO;
import com.example.webshop.helper.SearchUtil;
import com.example.webshop.model.Candidate;
import com.example.webshop.repository.CandidateDocumentRepository;
import com.example.webshop.repository.CandidateRepository;
import com.example.webshop.service.ElasticsearchService;
import org.apache.pdfbox.io.IOUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.elasticsearch.search.SearchHit;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public List<SearchResponseDTO> executeSearchQuery(SearchQueryDTO searchQuery) throws Exception {
        SearchRequest request = SearchUtil.buildSearchRequest(searchQuery);
        if (request == null) {
            LOG.error("Search request creation failed");
            return null;
        }

        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] searchHits = response.getHits().getHits();
            List<SearchResponseDTO> dtoList = new ArrayList<>();
            for (SearchHit hit : searchHits) {
                Map<String, Object> source = hit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();

                SearchResponseDTO responseDTO = new SearchResponseDTO(
                        (String) source.get("id"),
                        (String) source.get("first_name"),
                        (String) source.get("last_name"),
                        (String) source.get("email"),
                        (String) source.get("field_of_work"),
                        (Integer) source.get("education_level"),
                        (String) source.get("country"),
                        (String) source.get("city"),
                        (String) source.get("address"),
                        ""
                        );
                dtoList.add(responseDTO);
            }
            return dtoList;
        } catch (Error e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ResponseEntity<String> populateIndexFromDatabase() throws Exception {
        List<Candidate> candidates = candidateRepository.findAll();
        return null;
    }

    public byte[] getCVByID(Long id) {
        String userDirectory = System.getProperty("user.dir");
        Candidate candidate = candidateRepository.getById(id.toString());
        String path = candidate.getCv_location();
        File file = new File("src/main/resources/" + path);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] contents = new byte[(int) file.length()];
            inputStream.read(contents);
            inputStream.close();
            return contents;
        } catch (Exception e) {
            return null;
        }
    }
    public byte[] getCoverByID(Long id) {
        String userDirectory = System.getProperty("user.dir");
        Candidate candidate = candidateRepository.getById(id.toString());
        String path = candidate.getCover_location();
        File file = new File("src/main/resources/" + path);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] contents = new byte[(int) file.length()];
            inputStream.read(contents);
            inputStream.close();
            return contents;
        } catch (Exception e) {
            return null;
        }
    }
}
