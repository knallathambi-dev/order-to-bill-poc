// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productinventory.service.impl;

import com.orange.discobole.productinventory.dto.v1.PartyRef;
import com.orange.discobole.productinventory.dto.v1.PartyRoleRef;
import com.orange.discobole.productinventory.dto.v1.Product;
import com.orange.discobole.productinventory.dto.v1.RelatedPartyOrPartyRole;
import com.orange.discobole.productinventory.repository.ProductRepository;
import com.orange.discobole.productinventory.service.SecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    private static final String RELATED_PARTY_ID = "user-123";
    private static final String OTHER_USER_ID = "user-456";
    private static final String PRODUCT_ID = "product-789";
    private static final String RELATED_PARTY_PARAM = "relatedParty.id";

    @Mock
    private SecurityService securityService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AuthorizationService authorizationService;


    @Nested
    @DisplayName("validateFetchRequest Tests")
    class ValidateFetchRequestTests {

        @Test
        @DisplayName("Should allow admin user without modification")
        void shouldAllowAdminUserWithoutModification() {
            // Given
            when(securityService.isAdmin()).thenReturn(true);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add(RELATED_PARTY_PARAM, OTHER_USER_ID);

            // When
            authorizationService.validateFetchRequest(params, RELATED_PARTY_PARAM);

            // Then
            assertEquals(OTHER_USER_ID, params.getFirst(RELATED_PARTY_PARAM));
            verify(securityService, never()).getRelatedPartyId();
        }

        @Test
        @DisplayName("Should override relatedParty parameter for non-admin user")
        void shouldOverrideRelatedPartyParameterForNonAdminUser() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

            // When
            authorizationService.validateFetchRequest(params, RELATED_PARTY_PARAM);

            // Then
            assertEquals(RELATED_PARTY_ID, params.getFirst(RELATED_PARTY_PARAM));
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when non-admin user requests another user's data")
        void shouldThrowExceptionWhenNonAdminUserRequestsAnotherUsersData() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add(RELATED_PARTY_PARAM, OTHER_USER_ID);

            // When & Then
            assertThrows(AccessDeniedException.class, () ->
                    authorizationService.validateFetchRequest(params, RELATED_PARTY_PARAM)
            );
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user has no relatedPartyId")
        void shouldThrowExceptionWhenUserHasNoRelatedPartyId() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.empty());
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

            // When & Then
            assertThrows(AccessDeniedException.class, () ->
                    authorizationService.validateFetchRequest(params, RELATED_PARTY_PARAM)
            );
        }

        @Test
        @DisplayName("Should allow non-admin user to request their own data")
        void shouldAllowNonAdminUserToRequestOwnData() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add(RELATED_PARTY_PARAM, RELATED_PARTY_ID);

            // When
            authorizationService.validateFetchRequest(params, RELATED_PARTY_PARAM);

            // Then
            assertEquals(RELATED_PARTY_ID, params.getFirst(RELATED_PARTY_PARAM));
        }
    }

    @Nested
    @DisplayName("validateByIdRequest Tests")
    class ValidateByIdRequestTests {

        @Test
        @DisplayName("Should allow admin user without validation")
        void shouldAllowAdminUserWithoutValidation() {
            // Given
            when(securityService.isAdmin()).thenReturn(true);

            // When
            authorizationService.validateByIdRequest(PRODUCT_ID);

            // Then
            verify(productRepository, never()).existsByIdAndRelatedPartyId(anyString(), anyString());
        }

        @Test
        @DisplayName("Should allow non-admin user to access their own product")
        void shouldAllowNonAdminUserToAccessOwnProduct() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            when(productRepository.existsByIdAndRelatedPartyId(PRODUCT_ID, RELATED_PARTY_ID)).thenReturn(true);

            // When
            authorizationService.validateByIdRequest(PRODUCT_ID);

            // Then
            verify(productRepository).existsByIdAndRelatedPartyId(PRODUCT_ID, RELATED_PARTY_ID);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when product doesn't belong to user")
        void shouldThrowExceptionWhenProductDoesNotBelongToUser() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            when(productRepository.existsByIdAndRelatedPartyId(PRODUCT_ID, RELATED_PARTY_ID)).thenReturn(false);

            // When & Then
            assertThrows(AccessDeniedException.class, () ->
                    authorizationService.validateByIdRequest(PRODUCT_ID)
            );
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user has no relatedPartyId")
        void shouldThrowExceptionWhenUserHasNoRelatedPartyId() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.empty());

            // When & Then
            assertThrows(AccessDeniedException.class, () ->
                    authorizationService.validateByIdRequest(PRODUCT_ID)
            );
        }
    }

    @Nested
    @DisplayName("validateProductCreation Tests")
    class ValidateProductCreationTests {

        @Test
        @DisplayName("Should allow admin to create product for any user")
        void shouldAllowAdminToCreateProductForAnyUser() {
            // Given
            when(securityService.isAdmin()).thenReturn(true);
            Product product = createProductWithRelatedParty(OTHER_USER_ID);

            // When
            authorizationService.validateProductCreation(product);

            // Then
            verify(securityService, never()).getRelatedPartyId();
        }

        @Test
        @DisplayName("Should allow user to create product for themselves")
        void shouldAllowUserToCreateProductForThemselves() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            Product product = createProductWithRelatedParty(RELATED_PARTY_ID);

            // When
            authorizationService.validateProductCreation(product);

            // Then - No exception thrown
            verify(securityService).getRelatedPartyId();
        }

        @Test
        @DisplayName("Should allow product creation without relatedParty")
        void shouldAllowProductCreationWithoutRelatedParty() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            Product product = Product.builder().build();

            // When
            authorizationService.validateProductCreation(product);

            // Then - No exception thrown
            verify(securityService).getRelatedPartyId();
        }

        @Test
        @DisplayName("Should allow product creation with empty relatedParty list")
        void shouldAllowProductCreationWithEmptyRelatedPartyList() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            Product product = Product.builder().relatedParty(List.of()).build();

            // When
            authorizationService.validateProductCreation(product);

            // Then - No exception thrown
            verify(securityService).getRelatedPartyId();
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user tries to create product for another user")
        void shouldThrowExceptionWhenUserTriesToCreateProductForAnotherUser() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            Product product = createProductWithRelatedParty(OTHER_USER_ID);

            // When & Then
            assertThrows(AccessDeniedException.class, () ->
                    authorizationService.validateProductCreation(product)
            );
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user has no relatedPartyId in token")
        void shouldThrowExceptionWhenUserHasNoRelatedPartyIdInToken() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.empty());
            Product product = createProductWithRelatedParty(RELATED_PARTY_ID);

            // When & Then
            assertThrows(AccessDeniedException.class, () ->
                    authorizationService.validateProductCreation(product)
            );
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when product has multiple relatedParties including another user")
        void shouldThrowExceptionWhenProductHasMultipleRelatedPartiesIncludingAnotherUser() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));

            RelatedPartyOrPartyRole ownParty = createRelatedPartyOrPartyRole(RELATED_PARTY_ID);
            RelatedPartyOrPartyRole otherParty = createRelatedPartyOrPartyRole(OTHER_USER_ID);

            Product product = Product.builder()
                    .relatedParty(List.of(ownParty, otherParty))
                    .build();

            // When & Then
            assertThrows(AccessDeniedException.class, () ->
                    authorizationService.validateProductCreation(product)
            );
        }

        @Test
        @DisplayName("Should handle PartyRoleRef in relatedParty")
        void shouldHandlePartyRoleRefInRelatedParty() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));

            PartyRoleRef partyRoleRef = PartyRoleRef.builder()
                    .id(RELATED_PARTY_ID)
                    .build();

            RelatedPartyOrPartyRole relatedParty = RelatedPartyOrPartyRole.builder()
                    .partyOrPartyRole(partyRoleRef)
                    .build();

            Product product = Product.builder()
                    .relatedParty(List.of(relatedParty))
                    .build();

            // When
            authorizationService.validateProductCreation(product);

            // Then - No exception thrown, getRelatedPartyId called for validation
            verify(securityService).getRelatedPartyId();
        }

        @Test
        @DisplayName("Should handle null partyOrPartyRole gracefully")
        void shouldHandleNullPartyOrPartyRoleGracefully() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));

            RelatedPartyOrPartyRole relatedParty = RelatedPartyOrPartyRole.builder()
                    .partyOrPartyRole(null)
                    .build();

            Product product = Product.builder()
                    .relatedParty(List.of(relatedParty))
                    .build();

            // When
            authorizationService.validateProductCreation(product);

            // Then - No exception thrown (null IDs are ignored), getRelatedPartyId called for validation
            verify(securityService).getRelatedPartyId();
        }
    }

    @Nested
    @DisplayName("validateProductUpdate Tests")
    class ValidateProductUpdateTests {

        @Test
        @DisplayName("Should allow admin to update any product")
        void shouldAllowAdminToUpdateAnyProduct() {
            // Given
            when(securityService.isAdmin()).thenReturn(true);

            // When
            authorizationService.validateProductUpdate(PRODUCT_ID);

            // Then
            verify(productRepository, never()).existsByIdAndRelatedPartyId(anyString(), anyString());
        }

        @Test
        @DisplayName("Should allow user to update their own product")
        void shouldAllowUserToUpdateOwnProduct() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            when(productRepository.existsByIdAndRelatedPartyId(PRODUCT_ID, RELATED_PARTY_ID)).thenReturn(true);

            // When
            authorizationService.validateProductUpdate(PRODUCT_ID);

            // Then
            verify(productRepository).existsByIdAndRelatedPartyId(PRODUCT_ID, RELATED_PARTY_ID);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user tries to update another user's product")
        void shouldThrowExceptionWhenUserTriesToUpdateAnotherUsersProduct() {
            // Given
            when(securityService.isAdmin()).thenReturn(false);
            when(securityService.getRelatedPartyId()).thenReturn(Optional.of(RELATED_PARTY_ID));
            when(productRepository.existsByIdAndRelatedPartyId(PRODUCT_ID, RELATED_PARTY_ID)).thenReturn(false);

            // When & Then
            assertThrows(AccessDeniedException.class, () ->
                    authorizationService.validateProductUpdate(PRODUCT_ID)
            );
        }
    }

    // Helper methods
    private Product createProductWithRelatedParty(String relatedPartyId) {
        RelatedPartyOrPartyRole relatedParty = createRelatedPartyOrPartyRole(relatedPartyId);
        return Product.builder()
                .relatedParty(List.of(relatedParty))
                .build();
    }

    private RelatedPartyOrPartyRole createRelatedPartyOrPartyRole(String id) {
        PartyRef partyRef = PartyRef.builder()
                .id(id)
                .build();

        return RelatedPartyOrPartyRole.builder()
                .partyOrPartyRole(partyRef)
                .build();
    }
}
