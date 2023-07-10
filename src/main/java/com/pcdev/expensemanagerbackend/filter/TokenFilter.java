package com.pcdev.expensemanagerbackend.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.gson.Gson;
import com.pcdev.expensemanagerbackend.model.auth.MessageBody;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;
    private final Gson gson;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("Inside Filter");

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeJsonResponse(response, new MessageBody("Missing or Invalid Authorization Header"));
            return;
        }

        jwt = authHeader.substring(7);

        try {

            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(jwt);
            String userId = firebaseToken.getUid();
            System.out.println("userId: " + userId);
            mutableRequest.putHeader("userId", userId);
            filterChain.doFilter(mutableRequest, response);

        } catch (FirebaseAuthException firebaseAuthException) {
            System.out.println("FirebaseAuthException: " + firebaseAuthException.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJsonResponse(response, new MessageBody("Invalid Token"));
        }

    }


    private void writeJsonResponse(HttpServletResponse response, Object body) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(gson.toJson(body));
        writer.flush();
    }
}
