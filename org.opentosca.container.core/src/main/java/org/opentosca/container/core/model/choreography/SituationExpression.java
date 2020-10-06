package org.opentosca.container.core.model.choreography;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.ws.rs.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SituationExpression {

    public String expression;

    public String partner;

    public Map<String, URL> situationToUrlMap;

    public Collection<String> usedSituations;

    private final static Logger LOG = LoggerFactory.getLogger(SituationExpression.class);

    public SituationExpression(String expression, String partner, Map<String, String> tags) {
        this.expression = expression;
        this.partner = partner;
        this.usedSituations = this.findUsedSituations(this.expression);
        this.situationToUrlMap = this.mapSituationToEndpoint(this.usedSituations, tags);
    }

    private Map<String, URL> mapSituationToEndpoint(Collection<String> situations, Map<String, String> tags) {
        Map<String, URL> mapping = new HashMap<String, URL>();
        for (String situation : situations) {
            String urlString = tags.get(situation);
            try {
                URL url = new URL(urlString);
                mapping.put(situation, url);
            } catch (MalformedURLException e) {
                LOG.error("Couldn't parse situation url", e);
                continue;
            }
        }
        return mapping;
    }

    private Collection<String> findUsedSituations(String expression) {
        Collection<String> usedSituations = Lists.newArrayList();
        String workingCopy = new String(expression);
        workingCopy = workingCopy.replace("NOT", " ");
        workingCopy = workingCopy.replace("AND", " ");
        workingCopy = workingCopy.replace("OR", " ");

        for (String situation : workingCopy.split(" ")) {
            if (situation.trim().isEmpty()) continue;
            usedSituations.add(situation.trim());
        }

        return usedSituations;
    }

    public boolean evaluateExpression() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        String workingCopy = new String(this.expression);

        for (String usedSituation : this.usedSituations) {
            Boolean value = this.isSituationRuleActive(this.situationToUrlMap.get(usedSituation));
            engine.put(usedSituation, Boolean.valueOf(value));
        }

        workingCopy = workingCopy.replace("NOT", "!");
        workingCopy = workingCopy.replace("AND", "&&");
        workingCopy = workingCopy.replace("OR", "||");

        Object bool = engine.eval(workingCopy);

        return (Boolean) bool;
    }

    /**
     * Check if the situation on the given URL is active
     *
     * @param situationUrl the URL to the situation rule to evaluate
     * @return <code>true</code> if the referenced situation rule is active, <code>false</code> otherwise
     */
    private boolean isSituationRuleActive(URL situationUrl) {
        try {
            // retrieve situation
            HttpURLConnection connection = (HttpURLConnection) situationUrl.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(HttpMethod.GET);
            connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
            connection.connect();
            String json = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

            // read active part and parse to boolean to check if situation is active
            Map<String, Object> map = new ObjectMapper().readValue(json, Map.class);
            if (!map.containsKey("active")) {
                LOG.warn("Situation at URL '{}' is invalid!", situationUrl);
                return false;
            }
            return Boolean.parseBoolean(map.get("active").toString());
        } catch (IOException e) {
            LOG.debug("Unable to parse situation from URL {}: {}", situationUrl, e.getMessage());
            return false;
        }
    }
}
