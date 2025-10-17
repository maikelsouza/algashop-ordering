package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        urlPath("/api/v1/shopping-carts/f1a3b8e4-7c52-4a90-9d83-1e7c65a2b9d4/items")
    }
    response {
        status(200)
        headers {
            contentType("application/json")
        }

        body([
            items: [
                [
                        id: anyUuid(),
                        productId: anyUuid(),
                        name: "Notebook",
                        price: 500.00,
                        quantity: 2,
                        totalAmount: 1000.00,
                        available: anyBoolean()
                ],
                [
                        id: anyUuid(),
                        productId: anyUuid(),
                        name: "Mouse pad",
                        price: 250.00,
                        quantity: 1,
                        totalAmount: 250.00,
                        available: anyBoolean()
                ]
            ]
        ])
    }
}