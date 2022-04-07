package com.github.aha.sat.jpa.city;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@Slf4j
class CityRepositoryJpaTests extends AbstractRepositoryTests {

	static final int TOTAL_SIZE = 15;
	static long totalCount = -1;

	@PostConstruct
	void init() {
		if (totalCount < 0) {
			totalCount = cityRepository.count();
		}
	}
	
    @Test
	void countCities() {
		assertThat(totalCount).isEqualTo(TOTAL_SIZE);
    }

	@Nested
	class FindAllTest {


		@Test
		void pagination() {
			var pageSize = 5;

			Page<City> page = cityRepository.findAll(PageRequest.of(0, pageSize));

			assertThat(page.getSize()).isEqualTo(pageSize);
			assertThat(page.getTotalElements()).isEqualTo(TOTAL_SIZE);
			assertThat(page.getTotalPages()).isEqualTo(3);
			log.debug("\n### testPaging output");
			for (City city : page.getContent()) {
				log.debug(city.toString());
			}
		}

		@Test
		void sorting() {
			Page<City> page = cityRepository.findAll(PageRequest.of(0, 5, Sort.Direction.DESC, City_.COUNTRY, City_.NAME));

			assertThat(page.getSize()).isEqualTo(5);
			log.debug("\n### testSorting output");
			for (City city : page.getContent()) {
				log.debug(city.toString());
			}
		}

	}

	@Nested
	class FindByNameTest {

		@Test
		void shouldFindEntity() {
			var name = "Miami";

			var city = cityRepository.findByName(name);

			verifyCity(city, name, "USA");
		}

		@Test
		void shouldNotFindEntity() {
			var misspelledName = "prague";

			var city = cityRepository.findByName(misspelledName);

			assertThat(city).isNull();
		}

	}

	@Test
	void findByNameAndCountry() {
		var country = "USA";

		List<City> result = cityRepository.findByNameLikeAndCountryName("% %", country);

		assertThat(result).hasSize(2);
		verifyFirstCityInCollection(result, "New York", country);
	}

	@Test
	void findByNameAndCountryAllIgnoringCase() {
		City city = cityRepository.findByNameAndCountryNameAllIgnoringCase("Tokyo", "Japan");

		assertThat(city.getName()).isEqualTo("Tokyo");
		assertThat(city.getCountry().getName()).isEqualTo("Japan");
	}

	@Test
	void findByNameContainingAndCountryNameContainingAllIgnoringCase() {
		var pageSize = 2;

		Page<City> page = cityRepository.findByNameContainingAndCountryNameContainingAllIgnoringCase("an", "usa", PageRequest.of(0, pageSize));

		assertThat(page.getSize()).isEqualTo(pageSize);
		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getTotalPages()).isEqualTo(1);
		assertThat(page.getContent())
				.satisfies(c -> log.debug("The page content:"))
				.allSatisfy(city -> log.debug(city.toString()));
	}

	@Test
	void findByState() {
		List<City> result = cityRepository.findByState("California");

		assertThat(result).hasSize(1);
		verifyFirstCityInCollection(result, "San Francisco", "USA");
	}

	@Test
	void retrieveByName() {
		var name = "prague";

		var city = cityRepository.retrieveByName(name);

		verifyCity(city, "Prague", "Czech Republic");
	}

	@Nested
	class ModificationTest {

		@Test
		void createEntity() {
			var country = countryRepository.findByName("Australia");
			var city = City.builder()
					.name("Darwin")
					.state("North territory")
					.country(country)
					.build();
			country.getCities().add(city);

			countryRepository.save(country);

			assertThat(cityRepository.count()).isEqualTo(totalCount + 1);
		}

		@Test
		void deleteEntity() {
			var city = cityRepository.findByName("Prague");

			cityRepository.delete(city);

			assertThat(cityRepository.count()).isEqualTo(totalCount - 1);
		}

	}

}