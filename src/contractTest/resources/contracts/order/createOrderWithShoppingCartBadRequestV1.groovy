package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        url "/api/v1/orders"
        headers {
            contentType("application/vnd.order-with-shopping-cart.v1+json")
        }
        body([
                paymentMethod: ""
        ])
    }
    response {
        status 400
        headers {
            contentType 'application/problem+json'
        }
        body([
                instance: fromRequest().path(),
                type: "/errors/invalid-fields",
                title: "invalid fields",
                detail: "One or more fields are invalid",
                fields: [
                        shoppingCartId: anyNonBlankString(),
                        paymentMethod: anyNonBlankString(),
                        shipping: anyNonBlankString(),
                        billing: anyNonBlankString()
                ]
        ])
    }

}