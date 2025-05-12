package com.poly.controller;

import com.poly.model.*;
import com.poly.dao.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartDAO cartDAO;

    @Autowired
    private ProductDAO productDAO;
    
    @Autowired
    private ProductSizeDAO productSizeDAO;
    
    @Autowired
    private OrderDAO orderDAO;
    
    @Autowired
    private SizeDAO sizeDAO;
    
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Cart> cartItems = cartDAO.findByUser(user);
        model.addAttribute("cartItems", cartItems);
        Double total = cartDAO.calculateTotalByUser(user);
        model.addAttribute("total", total != null ? total : 0);
        return "cart";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Integer productId, 
                            @RequestParam("sizeId") Integer sizeId,
                            @RequestParam("quantity") Integer quantity,
                            HttpSession session, RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Product> productOptional = productDAO.findById(productId);
        Optional<Size> sizeOptional = sizeDAO.findById(sizeId);
        Optional<ProductSize> productSizeOptional = productSizeDAO.findByProduct_ProductIdAndSize_Id(productId, sizeId);

        if (productOptional.isEmpty() || sizeOptional.isEmpty() || productSizeOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm hoặc kích thước không hợp lệ!");
            return "redirect:/";
        }

        Product product = productOptional.get();
        Size size = sizeOptional.get();
        ProductSize productSize = productSizeOptional.get();

        if (quantity > productSize.getStockQuantity()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không đủ hàng tồn kho!");
            return "redirect:/";
        }

        Cart existingCartItem = cartDAO.findByUserAndProductAndSize(user, product, size);
        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartDAO.save(existingCartItem);
        } else {
            Cart cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setSize(size);
            cartItem.setQuantity(quantity);
            cartDAO.save(cartItem);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng!");
        return "redirect:/cart";
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public Map<String, Object> updateQuantity(@PathVariable("id") Integer cartId,
                                              @RequestParam("quantity") Integer quantity, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Optional<Cart> cartOptional = cartDAO.findById(cartId);
        if (cartOptional.isPresent()) {
            Cart cartItem = cartOptional.get();
            cartItem.setQuantity(quantity);
            cartDAO.save(cartItem);

            User user = (User) session.getAttribute("user");
            double updatedTotal = cartDAO.calculateTotalByUser(user);
            double itemTotal = cartItem.getProduct().getPrice() * quantity;

            response.put("status", "success");
            response.put("updatedTotal", updatedTotal);
            response.put("itemTotal", itemTotal);
            return response;
        }

        response.put("status", "error");
        return response;
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") Integer cartId, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Cart> cartOptional = cartDAO.findById(cartId);
        if (cartOptional.isPresent()) {
            Cart cartItem = cartOptional.get();
            if (cartItem.getUser().getUserId().equals(user.getUserId())) {
                cartDAO.delete(cartItem);
                redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền xóa sản phẩm này.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm trong giỏ hàng.");
        }
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("cartItems", cartDAO.findByUser(user));
        model.addAttribute("total", cartDAO.calculateTotalByUser(user));
        
        return "checkout";
    }
}
