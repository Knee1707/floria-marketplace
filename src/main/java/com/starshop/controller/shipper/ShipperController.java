package com.starshop.controller.shipper;

import com.starshop.domain.DeliveryAssignment;
import com.starshop.domain.enums.CommonEnums.DeliveryStatus;
import com.starshop.security.CurrentUser;
import com.starshop.service.common.NotificationService;
import com.starshop.service.shipper.ShipperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller Kênh Giao Vận (Shipper - D5):
 * F5.1 Đơn được phân công, F5.2 Cập nhật trạng thái giao hàng, F5.3 Thống kê giao vận.
 * @owner: TV2 | @leaf: F5.1–F5.3 | @status: DONE
 */
@Controller
@RequestMapping("/shipper")
@RequiredArgsConstructor
public class ShipperController {

    private final ShipperService shipperService;
    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    // @owner: TV2 | @leaf: F5.1 Đơn được phân công | @status: DONE
    @GetMapping({"", "/", "/orders"})
    public String assignedOrders(@RequestParam(required = false) DeliveryStatus status, Model model) {
        List<DeliveryAssignment> all = shipperService.myAssignments();
        List<DeliveryAssignment> filtered = status == null
                ? all
                : all.stream().filter(a -> a.getStatus() == status).toList();

        long assignedCount = all.stream().filter(a -> a.getStatus() == DeliveryStatus.ASSIGNED).count();
        long deliveringCount = all.stream().filter(a -> a.getStatus() == DeliveryStatus.DELIVERING).count();

        model.addAttribute("assignments", filtered);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", DeliveryStatus.values());
        model.addAttribute("assignedCount", assignedCount);
        model.addAttribute("deliveringCount", deliveringCount);
        model.addAttribute("deliveredThisMonth", shipperService.countDeliveredThisMonth());
        model.addAttribute("notifications", notificationService.myNotifications().stream().limit(6).toList());
        model.addAttribute("unreadCount", notificationService.unreadCount(currentUser.id()));
        return "shipper/assigned-orders";
    }

    // Shipper tự nhận đơn: danh sách đơn chờ nhận
    @GetMapping("/available")
    public String available(Model model) {
        model.addAttribute("orders", shipperService.availableOrders());
        return "shipper/available-orders";
    }

    @PostMapping("/orders/{id}/claim")
    public String claim(@PathVariable Long id, RedirectAttributes ra) {
        try {
            shipperService.claimOrder(id);
            ra.addFlashAttribute("toast", "Đã nhận đơn. Bấm \"Bắt đầu giao hàng\" khi đã lấy hàng.");
            return "redirect:/shipper/orders";
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
            return "redirect:/shipper/available";
        }
    }

    // @owner: TV2 | @leaf: F5.2 Cập nhật trạng thái giao | @status: DONE
    @PostMapping("/assignments/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam DeliveryStatus status,
                               RedirectAttributes ra) {
        try {
            shipperService.updateDelivery(id, status);
            ra.addFlashAttribute("toast", "Đã cập nhật trạng thái giao hàng sang: " + status);
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("toastError", ex.getMessage());
        }
        return "redirect:/shipper/orders";
    }

    // @owner: TV2 | @leaf: F5.3 Thống kê giao vận | @status: DONE
    @GetMapping("/stats")
    public String stats(Model model) {
        List<DeliveryAssignment> all = shipperService.myAssignments();
        long totalCount = all.size();
        long assignedCount = all.stream().filter(a -> a.getStatus() == DeliveryStatus.ASSIGNED).count();
        long deliveringCount = all.stream().filter(a -> a.getStatus() == DeliveryStatus.DELIVERING).count();
        long deliveredCount = all.stream().filter(a -> a.getStatus() == DeliveryStatus.DELIVERED).count();
        long failedCount = all.stream().filter(a -> a.getStatus() == DeliveryStatus.FAILED).count();

        BigDecimal totalDeliveredValue = all.stream()
                .filter(a -> a.getStatus() == DeliveryStatus.DELIVERED && a.getOrder() != null)
                .map(a -> a.getOrder().getTotal() != null ? a.getOrder().getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalShippingFeeEarned = all.stream()
                .filter(a -> a.getStatus() == DeliveryStatus.DELIVERED && a.getOrder() != null)
                .map(a -> a.getOrder().getShippingFee() != null ? a.getOrder().getShippingFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long finishedCount = deliveredCount + failedCount;
        int successRate = finishedCount > 0 ? (int) Math.round((deliveredCount * 100.0) / finishedCount) : 100;

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("assignedCount", assignedCount);
        model.addAttribute("deliveringCount", deliveringCount);
        model.addAttribute("deliveredCount", deliveredCount);
        model.addAttribute("failedCount", failedCount);
        model.addAttribute("deliveredThisMonth", shipperService.countDeliveredThisMonth());
        model.addAttribute("totalDeliveredValue", totalDeliveredValue);
        model.addAttribute("totalShippingFeeEarned", totalShippingFeeEarned);
        model.addAttribute("successRate", successRate);
        model.addAttribute("recentDelivered", all.stream()
                .filter(a -> a.getStatus() == DeliveryStatus.DELIVERED)
                .limit(10)
                .toList());
        return "shipper/stats";
    }

    // @owner: TV2 | @leaf: F7.1 Đánh dấu đã đọc thông báo | @status: DONE
    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id, RedirectAttributes ra) {
        notificationService.markRead(id);
        ra.addFlashAttribute("toast", "Đã đánh dấu thông báo là đã đọc.");
        return "redirect:/shipper/orders";
    }
}
