package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.QueryExecuter;
import ontoplay.OntoplayConfig;
import ontoplay.models.ClassCondition;
import ontoplay.models.ConditionDeserializer;
import ontoplay.models.ontologyModel.OntoClass;
import ontoplay.models.ontologyModel.OwlIndividual;
import ontoplay.models.ontologyReading.OntologyReader;
import models.IndividualViewModel;
import models.Target;
import ontoplay.models.owlGeneration.OntologyGenerator;
import play.mvc.Result;
import play.mvc.Results;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import play.Routes;

import ontoplay.controllers.OntologyController;
import ontoplay.controllers.utils.OntologyUtils;

import javax.inject.Inject;

//TODO: This should generate individual describing the configuration, instead of constraints
public class OntoPlay extends OntologyController {

	private OntologyUtils ontologyUtils;
	private OntologyReader ontologyReader;
	private OntoplayConfig config;
	private OntologyGenerator ontologyGenerator;

	@Inject
	public OntoPlay(OntologyUtils ontologyUtils, OntologyReader ontologyReader, OntoplayConfig config, OntologyGenerator ontologyGenerator){
		super(ontologyUtils);

		this.ontologyUtils = ontologyUtils;
		this.ontologyReader = ontologyReader;
		this.config = config;
		this.ontologyGenerator = ontologyGenerator;
	}

	public Result javascriptRoutes()
    {
        response().setContentType("text/javascript");
        return ok(
            Routes.javascriptRouter("jsRoutes",
		        // Routes
		        //controllers.routes.javascript.Application.condition()//,
		        controllers.routes.javascript.OntoPlay.add()
		        //controllers.routes.javascript.Application.getPropertyCondition(),
		        //controllers.routes.javascript.Application.getValueCondition(),
        ));
    }
	public Result sendOffer(String targetURI, String offerURI)
	{
		try
		{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		File file = new File(config.getOntologyFilePath());
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(file);
		file =null;
		OWLDataFactory factory = manager.getOWLDataFactory();
		OwlIndividual offerInd = ontologyReader.getIndividual(offerURI);
		OwlIndividual targetInd = ontologyReader.getIndividual(targetURI);
		if (offerInd == null || offerInd.getIndividual() == null || targetInd == null || targetInd.getIndividual() == null ) {
			return ok("Individual Not Found");
		}
		
		SendOffer(targetInd,offerInd,factory,ont,manager);
		
		return Results.redirect("/individual/" + targetInd.getIndividual().getLocalName());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return Results.redirect("/");
		}
	}
	
	private void SendOffer(OwlIndividual targetInd,
			OwlIndividual offerInd, OWLDataFactory df, OWLOntology o, OWLOntologyManager m) {
		
		try
		{
			OWLNamedIndividual targetOWL = df.getOWLNamedIndividual(IRI.create(targetInd.getUri()));
			OWLDataProperty prop = df.getOWLDataProperty(IRI.create(config.getOntologyNamespace() + "isOfferSent"));
			if(prop == null)
				return;
			OWLAxiom assertion = df.getOWLDataPropertyAssertionAxiom(prop, targetOWL, true);
			
			AddAxiom addAxiomChange = new AddAxiom(o, assertion);
			m.applyChange(addAxiomChange);
			OutputStream out =  new FileOutputStream(config.getOntologyFilePath());
			m.saveOntology(o, out);
			out.close();
		}
		catch(Exception ex){}

	}

	public Result remove(String individualUri, String className ) {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			File file = new File(config.getOntologyFilePath());
			OWLOntology ont = manager.loadOntologyFromOntologyDocument(file);
			file =null;
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLNamedIndividual individual=factory.getOWLNamedIndividual(IRI.create( config.getOntologyNamespace() + individualUri));
			
			
			
			if(individual == null)
				return Results.redirect("/view/" + className);
			OWLEntityRemover remover = new OWLEntityRemover(ont);
			individual.accept(remover);
			manager.applyChanges(remover.getChanges());
			OutputStream out =  new FileOutputStream(config.getOntologyFilePath());
			manager.saveOntology(ont, out);
			out.close();
			return Results.redirect("/view/" + className);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ok(e.toString());
		}
		
	}
	
	public Result view(String className) {
		
		//new JenaOwlReaderConfiguration().initialize(TANHelper.file,new JenaOwlReaderConfig().useLocalMapping(TANHelper.iriString,TANHelper.fileName));
		//Default class Name
		if (className == null)
			className = "http://www.w3.org/2002/07/owl#Thing";
		OntoClass owlClass = ontologyReader.getOwlClass(className);
		if (owlClass == null) {
			return ok("Class Not Found");
		}

		List<OwlIndividual> individuals = ontologyReader.getIndividuals(owlClass);
		return ok(views.html.tan.tableView.render(owlClass,(ArrayList<OwlIndividual>) individuals));
	}
	
	public Result individual(String individualName) {
		
		try
		{
			if (individualName == null)
				return ok("Individual name must be provided.");
			
			
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			File file = new File(config.getOntologyFilePath());
			OWLOntology ont = manager.loadOntologyFromOntologyDocument(file);
			
			OwlIndividual individual = ontologyReader.getIndividual(individualName);
			if (individual == null || individual.getIndividual() == null) {
				return ok("Individual Not Found");
			}
			
			IndividualViewModel ind = new IndividualViewModel(individual.getIndividual(), ontologyReader);
			
			if(ind.getClassUri().equalsIgnoreCase("http://www.tan.com#Target"))
				{
					Target target = new Target(ind, new QueryExecuter(config));
					return ok(views.html.tan.targetView.render(ind, target));
				}
			
			return ok(views.html.tan.instancesView.render(ind));
		}catch(Exception ex)
		{ return Results.redirect("/"); }
	}
	



	public Result add(String conditionJson, String individualName) {
		try {
			ClassCondition condition = ConditionDeserializer.deserializeCondition(ontologyReader, conditionJson);
			if(3>2)
				return ok("hello");
			OWLOntology generatedOntology = ontologyGenerator.convertToOwlIndividualOntology(ontologyUtils.joinNamespaceAndName(config.getOntologyNamespace(), individualName), condition);

			try {
				OwlIndividual individual = ontologyReader.getIndividual(ontologyUtils.joinNamespaceAndName(config.getOntologyNamespace(), individualName));
				if (individual != null)
					return ok("Indvidual name is already used");
			} catch (Exception e) {
			}
			
			if(generatedOntology == null)
			return ok("true");

			ontologyUtils.checkOntology(generatedOntology);
			OntologyReader checkOntologyReader = ontologyUtils.checkOwlReader();
			OntoClass owlClass = checkOntologyReader.getOwlClass("Offer");
			ontologyUtils.saveOntology(generatedOntology);
			// Fix nested individuals

			return ok("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return ok("Error");
		}
	}

	public Result show(String individualName) {
		try {
			OwlIndividual individual = ontologyReader
					.getIndividual(individualName);
			if (individual == null)
				return ok("No individual found");

		} catch (Exception ex) {

		}
		return ok("");
	}
	


}