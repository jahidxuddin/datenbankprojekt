import { z } from "zod";

const messageSchema = z.object({
  message: z.string(),
  statusCode: z.number(),
});

const tokenSchema = z.object({
  token: z.string(),
});

const userSchema = z.object({
  uuid: z.string().uuid(),
  firstName: z.string(),
  lastName: z.string(),
  email: z.string().email(),
  roles: z.string(),
  credits: z.number(),
});

const updateAccountDataRequestSchema = z.object({
  firstName: z.string(),
  lastName: z.string(),
  email: z.string().email(),
  password: z.string(),
});

const serverSchema = z.object({
  id: z.string().uuid(),
  name: z.string(),
  ram: z.number(),
  cpu: z.number(),
  storage: z.number(),
  price: z.number(),
  status: z.boolean(),
});

const serverArraySchema = z.array(serverSchema);

const serverRentSchema = z.object({
  token: z.string(),
  credits: z.number(),
});

const notificationSchema = z.object({
  id: z.string().uuid(),
  content: z.string(),
  createdAt: z.string(),
});

const notificationsArraySchema = z.array(notificationSchema);

const pricingSchema = z.object({
  id: z.string().uuid(),
  title: z.string(),
  details: z.string(),
  price: z.number(),
  createdAt: z.string(),
});

const pricingArraySchema = z.array(pricingSchema);

const costsSchema = z.object({
  date: z.string(),
  costs: z.number(),
});

const costsArraySchema = z.array(costsSchema);

export {
  messageSchema,
  tokenSchema,
  userSchema,
  updateAccountDataRequestSchema,
  notificationsArraySchema,
  serverArraySchema,
  serverRentSchema,
  pricingArraySchema,
  costsArraySchema,
};
