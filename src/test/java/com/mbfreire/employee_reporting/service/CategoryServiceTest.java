package com.mbfreire.employee_reporting.service;

import com.mbfreire.employee_reporting.entity.Category;
import com.mbfreire.employee_reporting.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryServiceTest {

    @Test
    void adminSeesInactiveCategoriesWhileReportSelectionDoesNot() {
        Category active = category("Ativa", true);
        Category inactive = category("Inativa", false);
        CategoryService service = new CategoryService(repository(List.of(active, inactive), new AtomicInteger()));
        var page = PageRequest.of(0, 20);

        assertEquals(List.of("Ativa", "Inativa"), service.findAllForAdmin(page)
                .map(response -> response.name()).getContent());
        assertEquals(List.of("Ativa"), service.findAll(page)
                .map(response -> response.name()).getContent());
    }

    @Test
    void deactivationPreservesCategoryAndIsIdempotent() {
        Category category = category("Conduta interna", true);
        AtomicInteger saves = new AtomicInteger();
        CategoryService service = new CategoryService(repository(List.of(category), saves));

        service.deactivate(category.getId());
        service.deactivate(category.getId());

        assertEquals(false, category.isActive());
        assertEquals(1, saves.get());
    }

    private Category category(String name, boolean active) {
        return Category.builder().id(UUID.randomUUID()).name(name).active(active).build();
    }

    private CategoryRepository repository(List<Category> categories, AtomicInteger saves) {
        return (CategoryRepository) Proxy.newProxyInstance(
                CategoryRepository.class.getClassLoader(),
                new Class<?>[]{CategoryRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findAll" -> new PageImpl<>(categories);
                    case "findByActiveTrue" -> new PageImpl<>(categories.stream().filter(Category::isActive).toList());
                    case "findById" -> categories.stream()
                            .filter(category -> category.getId().equals(args[0]))
                            .findFirst();
                    case "save" -> {
                        saves.incrementAndGet();
                        yield args[0];
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
