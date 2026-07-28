# VeloxAuto Frontend
Next.js App Router frontend for the VeloxAuto dealership platform.

## Stack
- Next.js (App Router, TypeScript)
- Tailwind CSS
- zod
- react-hook-form + @hookform/resolvers
- react-hot-toast


## Local development
From `frontend/`:

```bash
npm install
npm run dev
```

Default app URL: `http://localhost:3000`

## Environment
Create `.env.local` in `frontend/`:

```bash
NEXT_PUBLIC_API_BASE_URL=http://localhost:8081
```

## Scripts
```bash
npm run lint
npm run typecheck
npm run build
```
