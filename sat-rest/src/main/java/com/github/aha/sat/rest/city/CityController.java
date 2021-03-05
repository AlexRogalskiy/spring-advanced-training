package com.github.aha.sat.rest.city;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.aha.sat.rest.city.resource.CityBaseResource;
import com.github.aha.sat.rest.city.resource.CityProjections.Basic;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Usage:
 * get city detail				- GET http://localhost:8080/city/swagger/105
 * search 						- GET http://localhost:8080/city/swagger?country=Spain
 * search & sort 				- GET http://localhost:8080/city/swagger?sorting=id
 * create city					- POST http://localhost:8080/city/swagger/100 + content
 * update city					- PUT http://localhost:8080/city/swagger/102?state=Valencia
 * delete city					- DELETE http://localhost:8080/city/swagger/100 
 */
@RestController
@RequestMapping("/city/swagger")
@Api(value = "city", description = "Endpoint for city management")
public class CityController {

    @Autowired
    private CityService cityService;

	@GetMapping(produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
	@JsonView(Basic.class)
    @ApiOperation(value = "Returns list of cities", notes = "Returns a list of found city details.", response = City.class, responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of the list of cities (with simple detail)") })
	public List<City> search(@ApiParam(name = "country", required = false) @PathParam("country") String country,
            @ApiParam(name = "sorting", required = false) @PathParam("sorting") String sorting) {
        return cityService.search(country, sorting);
    }

	@GetMapping(value = "/{id}", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
    @ApiOperation(value = "Returns city", notes = "Returns the city specified by ID .", response = City.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of city detail (full detail)"),
            @ApiResponse(code = 404, message = "City with given ID does not exist") })
	public City getOne(@PathVariable("id") long id) {
		return cityService.getOne(id);
    }

	@PostMapping(consumes = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
	@ResponseStatus(CREATED)
	@ApiOperation(value = "Create the city", notes = "Create the city with defined attributes (ID is erased if defined)", response = Void.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Successful creation of the city") })
	public void create(@Valid @RequestBody CityBaseResource resource, HttpServletRequest request, HttpServletResponse response) {
		Long id = cityService.save(null, resource).getId();
		response.addHeader("Location", getNewLocation(request, id));
	}

	@PutMapping(value = "/{id}")
    @ApiOperation(value = "Update the city", notes = "Update the city with defined attributes (for defined ID)", response = Void.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful update of the city"),
            @ApiResponse(code = 409, message = "When ID in path is not equal to ID in the content (body)") })
	public City update(@PathVariable long id, CityBaseResource resource) {
		return cityService.save(id, resource);
    }

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(NO_CONTENT)
	@ApiOperation(value = "Delete the city", notes = "Delete the city defined by ID)", response = Void.class)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "Successful deletion of the city") })
	public void delete(@PathVariable long id) {
		cityService.delete(id);
	}

	private String getNewLocation(HttpServletRequest request, Long id) {
		StringBuffer url = request.getRequestURL();
		UriTemplate template = new UriTemplate(url.append("/{childId}").toString());
		return template.expand(id).toASCIIString();

	}

}