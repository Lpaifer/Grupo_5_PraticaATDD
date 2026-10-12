import assert from 'node:assert/strict'

const endpoints = {
  api: process.env.SMOKE_API_URL || 'http://api:8080',
  frontend: process.env.SMOKE_FRONTEND_URL || 'http://frontend:80',
  prometheus: process.env.SMOKE_PROMETHEUS_URL || 'http://prometheus:9090',
  grafana: process.env.SMOKE_GRAFANA_URL || 'http://grafana:3000',
}

async function request(url, options = {}) {
  const response = await fetch(url, { ...options, signal: AbortSignal.timeout(5000) })
  assert.ok(response.ok, `${url}: HTTP ${response.status}`)
  return response
}

async function eventually(label, check) {
  let lastError
  for (let attempt = 0; attempt < 30; attempt++) {
    try {
      await check()
      console.log(`OK: ${label}`)
      return
    } catch (error) {
      lastError = error
      await new Promise(resolve => setTimeout(resolve, 2000))
    }
  }
  throw new Error(`Falha: ${label}`, { cause: lastError })
}

await eventually('API e banco saudáveis', async () => {
  const health = await (await request(`${endpoints.api}/actuator/health`)).json()
  assert.equal(health.status, 'UP')
  await request(`${endpoints.api}/api/cursos`)
})
await eventually('Frontend e proxy Nginx', async () => {
  const html = await (await request(endpoints.frontend)).text()
  assert.match(html, /id="app"/)
  const cursos = await (await request(`${endpoints.frontend}/api/cursos`)).json()
  assert.ok(Array.isArray(cursos))
})
await eventually('Métricas JVM e HTTP disponíveis', async () => {
  const metrics = await (await request(`${endpoints.api}/actuator/prometheus`)).text()
  assert.match(metrics, /jvm_memory_used_bytes/)
  assert.match(metrics, /http_server_requests_seconds_count/)
})
await eventually('Prometheus coletando a API', async () => {
  const query = encodeURIComponent('up{job="grupo5-api"}')
  const result = await (await request(`${endpoints.prometheus}/api/v1/query?query=${query}`)).json()
  assert.equal(result.status, 'success')
  assert.equal(result.data.result.length, 1)
  assert.equal(result.data.result[0].value[1], '1')
})
const credentials = Buffer.from(
  `${process.env.GRAFANA_USER || 'admin'}:${process.env.GRAFANA_PASSWORD || 'admin_homol'}`,
).toString('base64')
const auth = { headers: { Authorization: `Basic ${credentials}` } }
await eventually('Grafana com datasource e dashboard provisionados', async () => {
  const health = await (await request(`${endpoints.grafana}/api/health`)).json()
  assert.equal(health.database, 'ok')
  const source = await (await request(`${endpoints.grafana}/api/datasources/uid/prometheus`, auth)).json()
  assert.equal(source.url, 'http://prometheus:9090')
  const dashboard = await (await request(`${endpoints.grafana}/api/dashboards/uid/grupo5-homol`, auth)).json()
  assert.equal(dashboard.dashboard.uid, 'grupo5-homol')
})
