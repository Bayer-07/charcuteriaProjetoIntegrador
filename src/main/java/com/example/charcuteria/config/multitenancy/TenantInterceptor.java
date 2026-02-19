package com.example.charcuteria.config.multitenancy;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.charcuteria.exceptions.TenantNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader("X-Tenant-ID"); // id do user que ta fazendo a requisição
        
        if (tenantId != null) {
            log.info("=> Requisição recebida para o Tenant: [{}] na rota: [{}]", tenantId, request.getRequestURI());
            TenantContext.setCurrentTenant(tenantId);
        } else {
            log.warn("!! Tentativa de acesso sem X-Tenant-ID na rota: [{}]", request.getRequestURI());
        }

        if (tenantId == null || tenantId.isEmpty()) {
            throw new TenantNotFoundException("O header com ID é obrigatório.");
        }   

        TenantContext.setCurrentTenant(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}