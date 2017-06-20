package models;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import ontoplay.controllers.utils.OntologyUtils;

import java.io.InputStream;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import play.Logger.ALogger;

public class QueryExecuter {
	
	static QueryExecuter connection = new QueryExecuter();
	
	OntModel connect() {
		OntModel mode = null;
		mode = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		InputStream in = FileManager.get().open(OntologyUtils.file); 
		if (in == null) {
			throw new IllegalArgumentException("File not found"); 
		}
		return (OntModel) mode.read(in, "");
	}

	ResultSet executeQuery(String queryString) {
		ALogger log = play.Logger.of("application");
		log.info( "\n" + queryString + "\n");
		Query query = QueryFactory.create(queryString);
		QueryExecution queryExcution = QueryExecutionFactory.create(query,connect());
		ResultSet resultSet = queryExcution.execSelect();
		return resultSet;
	}

}
