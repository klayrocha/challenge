# New class
<p>AccountNotFoundException to be thrown when not found account with id</p>
<p>ApiError to use in return body of exceptions</p>
<p>RestExceptionHandler to handler exceptions in general</p>
<p>TransactionRuleException to be thrown when rule exception happened</p>
<p>EntityLockWrapper to help handler concurrency thread</p>
<p>TransactionRequestVo to use for request transfer</p>
<p>TransactionController endpoint to handler transfer</p>
<p>TransactionRequestVoBuilder to help object creation in tests</p>
<p>TransactionControllerTest to test transfer endpoint</p>
<p>TransactionResourceConcurrentTest to test concurrency thread</p>
<p>junit-platform.properties to enabled parallel tests</p>

# Code Changes
<p>Inserted @RequiredArgsConstructor lombok annotation in a AccountsController class
in order to have less code</p>
<p>Inserted @RequiredArgsConstructor lombok annotation in a AccountsService class
in order to have less code</p>
<p>Changed getAccount in a AccountsService to handler not found account</p>
<p>Changed Account class to implement EntityLockWrapper.Lockable to handler
concurrency thread and added new attribute called ReentrantLock</p>
<p>Created new method called executeTransfer in a AccountsRepository class</p>
<p>Created new method called executeTransfer in a AccountsRepositoryInMemory class,
to implements interface</p>
<p>Created new method called <b>transfer</b> in AccountsService to handler transfer</p>
<p>Created new method called <b>sendNotification</b> in AccountsService to call 
notifyAboutTransfer method</p>
<p>Created new method called <b>isValueNegative</b> in AccountsService to validate
negative value</p>
<p>Created new method called <b>isBalanceNotSufficient</b> in AccountsService to validate
not sufficient value</p>
<p>Created new method called <b>unlockAccount</b> in AccountsService to handler
concurrency thread</p>
<p>Changed AccountsServiceTest to add new transfer tests</p>

# Changes before send production
<p>1 - Change it to use real database</p>
<p>2 - Create new entity called Client to associate it to Account</p>
<p>3 - Create endpoints to handler Client entity, maybe another microservice</p>