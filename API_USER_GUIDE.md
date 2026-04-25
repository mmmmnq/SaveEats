# API Documentation: User Profile, Orders & Favorites

This guide describes the endpoints available for managing user profile data, viewing order history, and interacting with favorite businesses.

## 1. User Profile

### Get Current User Profile
Returns the authenticated user's details.
- **Endpoint:** `GET /api/users/me`
- **Auth required:** Yes (Bearer Token)
- **Response (`UserResponse`):**
  ```json
  {
    "id": 1,
    "email": "user@example.com",
    "full_name": "Ivan Ivanov",
    "avatar_url": "https://example.com/avatar.jpg",
    "role": "CUSTOMER"
  }
  ```

### Update Avatar
Updates the avatar URL for the current user.
- **Endpoint:** `PUT /api/users/me/avatar`
- **Body:** `{"avatar_url": "string"}`
- **Response:** Updated `UserResponse`.

---

## 2. Order History

### Get My Orders
Returns a list of all orders made by the authenticated customer, including details of the associated offers and businesses.
- **Endpoint:** `GET /api/users/me/orders` (or `GET /api/orders/me`)
- **Auth required:** Yes (Role: `CUSTOMER`)
- **Response:** `Array<OrderWithOffer>`
  ```json
  [
    {
      "id": 10,
      "user_id": 1,
      "offer_id": 5,
      "quantity": 1,
      "total_price": 250.0,
      "status": "RESERVED",
      "pickup_code": "ABC123XY",
      "created_at": "2023-10-27T10:00:00Z",
      "offer": {
        "id": 5,
        "title": "Surprise Bag",
        "price": 250.0,
        "image_url": "https://...",
        "business": {
          "name": "Local Bakery",
          "address": "ul. Lenina, 10"
        }
      }
    }
  ]
  ```

---

## 3. Favorite Businesses

### Get Favorite Businesses
Returns a list of businesses that the user has added to their favorites.
- **Endpoint:** `GET /api/users/me/favorites`
- **Auth required:** Yes (Role: `CUSTOMER`)
- **Response:** `Array<BusinessResponse>`
  ```json
  [
    {
      "id": 1,
      "name": "Green Cafe",
      "address": "Street 1",
      "rating": 4.5,
      "logo_url": "..."
    }
  ]
  ```

### Toggle Favorite (Add/Remove)
A single endpoint to add a business to favorites or remove it if it's already there.
- **Endpoint:** `POST /api/users/me/favorites/{business_id}`
- **Auth required:** Yes (Role: `CUSTOMER`)
- **Response:**
  - `{"message": "Added to favorites", "is_favorite": true}`
  - `{"message": "Removed from favorites", "is_favorite": false}`

---

## Notes for Android Developer:
1. **Authentication:** All these requests require the `Authorization: Bearer <token>` header.
2. **Models:** The `OrderWithOffer` model is nested. Ensure your POJO/Data classes in Kotlin reflect the nesting of `offer` and `business` inside the order object.
3. **Toggle Logic:** For the "Heart" icon, you can simply call the `POST` endpoint; the backend handles the check whether to add or delete.
