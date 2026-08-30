export default function Toast({ message, type }) {
  return <div className={`toast toast--${type}`}>{message}</div>
}
