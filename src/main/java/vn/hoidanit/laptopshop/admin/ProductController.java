package vn.hoidanit.laptopshop.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.exception.ProductNotFoundException;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class ProductController {
    private final UploadService uploadService;
    private final ProductService productService;

    @Autowired
    public ProductController(UploadService uploadService, ProductService productService) {
        this.uploadService = uploadService;
        this.productService = productService;
    }

    @GetMapping("/admin/product")
    public String getProduct(@RequestParam(name = "page")  int pageNum ,Model model) {

        Pageable pageable = PageRequest.of(pageNum - 1, 5);
        Page<Product> page = productService.listByPage(pageable);

        List<Product> listProducts = page.getContent();

        model.addAttribute("listProducts", listProducts);

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());


        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getProductForm(Model model) {
        Product product = new Product();
        model.addAttribute("pageTitle", "Create a Product");
        model.addAttribute("product", product);
        return "admin/product/create";
    }

    @GetMapping("/admin/product/detail/{id}")
    public String getProduct(@PathVariable(name = "id") Long id, Model model) throws ProductNotFoundException {
        Product product = productService.get(id);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", String.format("Product Detail (ID: %d)", id));
        return "admin/product/detail";
    }

    @GetMapping("/admin/product/update/{id}")
    public String updateProduct(@PathVariable(name = "id") Long id, Model model) throws ProductNotFoundException {
        Product product = productService.get(id);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", String.format("Update product (ID: %d)", id));
        return "admin/product/create";
    }

    @PostMapping("/admin/product/save")
    public String save(
            @Valid Product productInForm,
            BindingResult bindingResult,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            Model model
    ) throws IOException, ProductNotFoundException {
        if(bindingResult.hasErrors()) {
            if(productInForm.getId() != null) {
                Product product = productService.get(productInForm.getId());
                productInForm.setImage(product.getImage());
                model.addAttribute("product", productInForm);
            }
            return "admin/product/create";
        }

        if(!imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis()+ "-" + StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
            String uploadDir = "/product";
            productInForm.setImage(fileName);
            uploadService.saveFile(uploadDir, fileName, imageFile);
        }

        productService.save(productInForm);
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String getDeletePage(@PathVariable(name = "id") Long id, Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Delete Product");
        model.addAttribute("id", id);
        return "admin/product/confirm_delete";
    }

    @PostMapping("/admin/product/delete")
    public String deleteProduct(Product product) {
        productService.deleteProduct(product.getId());
        return "redirect:/admin/product";
    }
}
