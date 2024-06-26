package de.ju.api.server;

import de.ju.api.bill.BillService;
import de.ju.api.bill.model.BillCreateRequest;
import de.ju.api.exception.EntityAlreadyExistsException;
import de.ju.api.exception.EntityNotExistsException;
import de.ju.api.pricing.Pricing;
import de.ju.api.pricing.PricingService;
import de.ju.api.server.exception.NotEnoughCreditsException;
import de.ju.api.server.model.ServerRentRequest;
import de.ju.api.server.model.ServerRentResponse;
import de.ju.api.server.model.ServerStatusRequest;
import de.ju.api.user.AppUser;
import de.ju.api.user.AppUserService;
import de.ju.api.user.model.CreditRequest;
import de.ju.api.userServerRelation.UserServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository repository;
    private final AppUserService userService;
    private final PricingService pricingService;
    private final BillService billService;
    private final UserServerService userServerService;

    public Server findServerById(UUID uuid) throws EntityNotExistsException {
        return repository.findById(uuid).orElseThrow(() -> new EntityNotExistsException("Server existiert nicht"));
    }

    public ServerRentResponse rentServer(ServerRentRequest request) throws EntityAlreadyExistsException, EntityNotExistsException, NotEnoughCreditsException {
        AppUser user = userService.findUserById(request.userId());

        Optional<Pricing> pricingOptional = pricingService.getPricingByUUID(request.pricingId());
        Pricing pricing = pricingOptional.orElseThrow(() -> new EntityNotExistsException("Preisgestaltung existiert nicht."));

        if (user.getCredits() < pricing.getPrice()) {
            throw new NotEnoughCreditsException("Benuter hat zu wenig Guthaben.");
        }

        String token = userService.updateCredits(new CreditRequest(-pricing.getPrice(), user.getId()));

        Server server = Server.builder().name(request.name()).storage(1).price(pricing.getPrice()).build();

        repository.save(server);

        userServerService.addUserServerRelation(user, server);

        billService.createBill(new BillCreateRequest(pricing.getPrice(), user.getId()));

        return new ServerRentResponse(token, user.getCredits() - pricing.getPrice());
    }

    public void updateServerStatus(UUID uuid, ServerStatusRequest request) throws EntityNotExistsException {
        Optional<Server> serverOptional = repository.findById(uuid);
        if (serverOptional.isEmpty()) {
            throw new EntityNotExistsException("Server existiert nicht");
        }

        Server updatedServer = serverOptional.get();
        updatedServer.setStatus(request.status());

        repository.save(updatedServer);
    }

    public void deleteServer(UUID uuid) throws EntityNotExistsException {
        Optional<Server> serverOptional = repository.findById(uuid);
        if (serverOptional.isEmpty()) {
            throw new EntityNotExistsException("Server existiert nicht");
        }

        repository.delete(serverOptional.get());
    }
}
