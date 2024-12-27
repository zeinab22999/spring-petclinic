
package org.springframework.samples.petclinic.vet;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class VetController1 {

	private static final int DEFAULT_PAGE_SIZE = 5;
	private static final String VET_LIST_VIEW = "vets/vetList";
	private static final String ATTRIBUTE_CURRENT_PAGE = "currentPage";
	private static final String ATTRIBUTE_TOTAL_PAGES = "totalPages";
	private static final String ATTRIBUTE_TOTAL_ITEMS = "totalItems";
	private static final String ATTRIBUTE_LIST_VETS = "listVets";

	private final VetRepository vetRepository;

	public VetController1(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	@GetMapping("/vets.html")
	public String showPaginatedVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		if (page < 1) {
			page = 1; // تأكد من أن الصفحة لا تكون أقل من 1
		}

		Page<Vet> paginatedVets = findPaginated(page);
		return prepareModelForView(page, paginatedVets, model);
	}

	private String prepareModelForView(int page, Page<Vet> paginatedVets, Model model) {
		model.addAttribute(ATTRIBUTE_CURRENT_PAGE, page);
		model.addAttribute(ATTRIBUTE_TOTAL_PAGES, paginatedVets.getTotalPages());
		model.addAttribute(ATTRIBUTE_TOTAL_ITEMS, paginatedVets.getTotalElements());
		model.addAttribute(ATTRIBUTE_LIST_VETS, paginatedVets.getContent());
		return VET_LIST_VIEW;
	}

	private Page<Vet> findPaginated(int page) {
		Pageable pageable = PageRequest.of(page - 1, DEFAULT_PAGE_SIZE);
		return vetRepository.findAll(pageable);
	}

	@GetMapping("/vets")
	public @ResponseBody Vets getVetsAsJson() {
		Page<Vet> paginatedVets = vetRepository.findAll(PageRequest.of(0, DEFAULT_PAGE_SIZE));
		return createVetsObject(paginatedVets.getContent());
	}

	private Vets createVetsObject(List<Vet> vetList) {
		Vets vets = new Vets();
		vets.getVetList().addAll(vetList);
		return vets;
	}
}
