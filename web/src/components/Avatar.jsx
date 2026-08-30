export default function Avatar({ name, size = 40, frame }) {
  const letter = (name?.[0] ?? '?').toUpperCase()
  const hue = name ? name.split('').reduce((a, c) => a + c.charCodeAt(0), 0) % 360 : 200

  return (
    <div
      className={`avatar${frame ? ` avatar--frame avatar--frame-${frame.toLowerCase()}` : ''}`}
      style={{
        width: size,
        height: size,
        fontSize: size * 0.42,
        background: `hsl(${hue}, 70%, 45%)`,
      }}
    >
      {letter}
    </div>
  )
}
