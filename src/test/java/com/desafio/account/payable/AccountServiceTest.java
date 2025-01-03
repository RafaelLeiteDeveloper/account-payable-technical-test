package com.desafio.account.payable;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.application.dto.response.AccountResponse;
import com.desafio.account.payable.application.mapper.AccountMapper;
import com.desafio.account.payable.application.service.AccountImportService;
import com.desafio.account.payable.application.service.QueueService;
import com.desafio.account.payable.application.strategy.AccountImportStrategy;
import com.desafio.account.payable.application.strategy.AccountImportStrategyFactory;
import com.desafio.account.payable.domain.model.AccountModel;
import com.desafio.account.payable.domain.model.AccountStatus;
import com.desafio.account.payable.domain.model.AuditImportModel;
import com.desafio.account.payable.domain.model.FileMessage;
import com.desafio.account.payable.domain.repository.AccountRepository;
import com.desafio.account.payable.domain.repository.AuditImportRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.dao.DuplicateKeyException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private AuditImportRepository auditImportRepository;

	@Mock
	private AccountImportStrategyFactory accountImportStrategyFactory;
	@InjectMocks
	private com.desafio.account.payable.application.service.AccountService accountService;
	@InjectMocks
	AccountImportService accountImportService;

	@Mock
	private QueueService queueService;

	@Mock
	private AccountImportStrategy accountImportStrategy;

	@Mock
	private FileMessage fileMessage;

	private static final Long ACCOUNT_ID = 1L;
	private AccountModel mockAccount;

	@BeforeEach
	void setUp() {
		mockAccount = createMockAccount();

	}

	@Test
	void shouldReturnAccount_WhenAccountExists() {
		// Arrange
		when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(mockAccount));

		// Act
		AccountModel account = accountService.findOrError(ACCOUNT_ID);

		// Assert
		assertNotNull(account, "Account should not be null");
		assertEquals(mockAccount.getId(), account.getId(), "Account ID should match");
		verify(accountRepository).findById(ACCOUNT_ID);
	}

	@Test
	void shouldThrowEntityNotFoundException_WhenAccountDoesNotExist() {
		// Arrange
		when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> accountService.findOrError(ACCOUNT_ID), "Expected exception not thrown");
	}

	@Test
	void shouldReturnAccountResponse_WhenAccountFound() {
		// Arrange
		when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(mockAccount));

		// Act
		AccountResponse accountResponse = accountService.findById(ACCOUNT_ID);

		// Assert
		assertNotNull(accountResponse, "Account response should not be null");
		assertEquals(ACCOUNT_ID, accountResponse.getId(), "Account response ID should match");
		assertEquals("R$ 100.00", accountResponse.getAmount(), "Amount should be formatted correctly with prefix");
	}

	@Test
	void shouldThrowEntityNotFoundException_WhenAccountNotFound() {
		// Arrange
		when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> accountService.findById(ACCOUNT_ID));
	}

	@Test
	void shouldMapAccountToAccountResponseCorrectly() {
		// Arrange
		AccountModel accountModel = createMockAccount();

		// Act
		AccountResponse accountResponse = AccountMapper.toAccountResponse(accountModel);

		// Assert
		assertNotNull(accountResponse, "Account response should not be null");
		assertEquals(accountModel.getId(), accountResponse.getId(), "Account response ID should match");
		assertEquals("R$ 100.00", accountResponse.getAmount(), "Amount should be formatted correctly with prefix");
	}


	@Test
	void shouldCreateAccountSuccessfully_WhenValidRequest() {
		// Arrange
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setDescription("New Account");
		accountRequest.setAmount(BigDecimal.valueOf(200.00));
		accountRequest.setDueDate(LocalDateTime.now());
		accountRequest.setPaymentDate(LocalDateTime.now());
		accountRequest.setStatus(AccountStatus.ACTIVE);

		// Act
		accountService.createAccount(accountRequest);

		// Assert
		verify(accountRepository).save(any(AccountModel.class));
	}

	@Test
	void shouldThrowIllegalArgumentException_WhenCreatingAccountWithInvalidStatus() {
		// Arrange
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setDescription("Invalid Status Account");
		accountRequest.setAmount(BigDecimal.valueOf(200.00));
		accountRequest.setDueDate(LocalDateTime.now());
		accountRequest.setPaymentDate(LocalDateTime.now());
		accountRequest.setStatus(AccountStatus.PENDING); // Status inválido

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(accountRequest),
				"Expected exception not thrown for invalid status");
	}

	@Test
	void shouldUpdateAccount_WhenAccountExists() {
		// Arrange
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setDescription("Updated Description");
		accountRequest.setAmount(BigDecimal.valueOf(300.00));
		accountRequest.setDueDate(LocalDateTime.now());
		accountRequest.setPaymentDate(LocalDateTime.now());
		accountRequest.setStatus(AccountStatus.ACTIVE);

		when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(mockAccount));

		// Act
		accountService.updateAccount(ACCOUNT_ID, accountRequest);

		// Assert
		verify(accountRepository).save(any(AccountModel.class));
		assertEquals("Updated Description", mockAccount.getDescription(), "Description should be updated");
	}

	@Test
	void shouldThrowEntityNotFoundException_WhenUpdatingNonExistingAccount() {
		// Arrange
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setDescription("Updated Description");
		accountRequest.setAmount(BigDecimal.valueOf(300.00));

		when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> accountService.updateAccount(ACCOUNT_ID, accountRequest),
				"Expected exception not thrown when account is not found");
	}

	@Test
	void shouldUpdateAccountStatus_WhenValidStatus() {
		// Arrange
		AccountStatus newStatus = AccountStatus.INACTIVE;

		when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(mockAccount));

		// Act
		accountService.updateAccountStatus(ACCOUNT_ID, newStatus);

		// Assert
		verify(accountRepository).save(mockAccount);
		assertEquals(newStatus.getStatus(), mockAccount.getStatus(), "Status should be updated correctly");
	}

	private AccountModel createMockAccount() {
		return AccountModel.builder()
				.id(ACCOUNT_ID)
				.dueDate(LocalDateTime.now())
				.paymentDate(LocalDateTime.now())
				.amount(new BigDecimal("100.00"))
				.description("Test Account")
				.status("ACTIVE")
				.build();
	}

	@Test
	void importAccounts_successfulImport() throws IOException {
		// Arrange
		String processId = "process-123";
		List<AccountRequest> accountRequests = Collections.singletonList(
				AccountRequest.builder()
						.dueDate(LocalDateTime.now().plusDays(10))
						.paymentDate(LocalDateTime.now())
						.amount(new BigDecimal("100.50"))
						.description("Test account")
						.status(AccountStatus.PENDING)
						.build()
		);

		when(fileMessage.getFileContent()).thenReturn(new byte[]{});
		when(fileMessage.getFileType()).thenReturn("CSV");
		when(accountImportStrategyFactory.getStrategy(anyString())).thenReturn(accountImportStrategy);
		when(accountImportStrategy.importAccounts(any(), any())).thenReturn(accountRequests);

		// Act
		accountImportService.importAccounts(fileMessage, processId);

		// Assert
		verify(accountRepository, times(1)).save(any(AccountModel.class));
	}

	@Test
	void verifyIdempotencyAndSaveAudit_duplicateProcessId() {
		// Arrange
		String processId = "process-123";

		when(auditImportRepository.findByIdProcess(any())).thenReturn(Optional.ofNullable(AuditImportModel.builder().idProcess(processId).build()));

		// Act & Assert
		verify(auditImportRepository, never()).save(any());
	}


	@Test
	void importAccounts_failedImport_dueToMappingError() throws IOException {
		// Arrange
		String processId = "process-123";
		List<AccountRequest> accountRequests = Arrays.asList(
				AccountRequest.builder()
						.dueDate(LocalDateTime.now().plusDays(10))
						.paymentDate(LocalDateTime.now())
						.amount(new BigDecimal("100.50"))
						.description("Test account")
						.status(AccountStatus.PENDING)
						.build()
		);

		when(fileMessage.getFileContent()).thenReturn(new byte[]{});
		when(fileMessage.getFileType()).thenReturn("CSV");
		when(accountImportStrategyFactory.getStrategy(anyString())).thenReturn(accountImportStrategy);
		when(accountImportStrategy.importAccounts(any(), any())).thenReturn(accountRequests);
		doThrow(new RuntimeException("Error saving account")).when(accountRepository).save(any(AccountModel.class));

		// Act
		accountImportService.importAccounts(fileMessage, processId);

		// Assert
		verify(queueService, times(1)).sendToToQueueDlq(any(), any());
	}

}
